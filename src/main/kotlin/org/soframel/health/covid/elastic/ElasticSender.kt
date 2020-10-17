package org.soframel.health.covid.elastic


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.reindex.DeleteByQueryRequest
import org.soframel.health.covid.model.CountryDailyData
import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import java.io.StringWriter
import java.time.LocalDate
import java.util.*
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class ElasticSender {
    val logger = Logger.getLogger(ElasticSender::class.qualifiedName)

    val ELASTIC_PASSWORD_PROPNAME="elastic.covid.password"

    @Inject
    @ConfigProperty(name = "elastic.hostname")
    var elasticHostname: String?=null;

    @Inject
    @ConfigProperty(name = "elastic.port")
    var elasticPort: String?=null

    @Inject
    @ConfigProperty(name = "elastic.scheme")
    var elasticScheme: String?=null

    @Inject
    @ConfigProperty(name = "elastic.username")
    var elasticUsername: String?=null

    //password is not injected but comes from a system property
    var elasticPassword: String?=null

    @Inject
    @ConfigProperty(name="elastic.covid.indexName")
    var indexName: String?=null

    var client: RestHighLevelClient?=null
    var options: RequestOptions?=null


    val mapper: ObjectMapper
    init{
        mapper= ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @PostConstruct
    fun postInjectInit(){
        elasticPassword=System.getProperty(ELASTIC_PASSWORD_PROPNAME)
        logger.info("building client with scheme $elasticScheme, hostname $elasticHostname, port $elasticPort and username $elasticUsername, password has "+elasticPassword?.length+" characters.")
        client= RestHighLevelClient(org.elasticsearch.client.RestClient.builder(org.apache.http.HttpHost(elasticHostname, java.lang.Integer.parseInt(elasticPort), elasticScheme)))

        val auth=elasticUsername+":"+elasticPassword
        val token= Base64.getEncoder().encodeToString(auth.toByteArray())
        val builder = RequestOptions.DEFAULT.toBuilder()
        builder.addHeader("Authorization", "Basic $token")
        options = builder.build()
    }

    fun serializeAndSend(data: CovidElasticData){

        val sw= StringWriter()
        mapper.writeValue(sw, data)
        sw.flush()
        val json=sw.toString()

        this.sendToElastic(json, data.country + "-" + data.source + "-" + data.date)
    }

    fun serializeAndSendBulk(list: List<CovidElasticData>){

        if(list!=null && list.size>0) {
            var request = BulkRequest()
            for (data in list) {
                request.add(IndexRequest(indexName).id(this.getDataId(data)).source(this.serializeDataToJson(data), XContentType.JSON))
            }
            logger.info("sending bulk data with "+list.size+" entries")
            val indexResponse = client?.bulk(request, options)
            val failed=indexResponse?.filter{it.isFailed}
            if(failed!=null && failed.isNotEmpty()){
                logger.severe("elastic response contained errors: ")
                for(f in failed){
                    logger.severe("failed: "+f.id+", failure="+f.failure+", message="+f.failureMessage)
                }
            }
            else {
                logger.info("elastic response ok")
            }
        }
        else{
            logger.warning("serializeAndSendBulk: list is empty")
        }
    }

    fun getDataId(data: CovidElasticData): String{
        return data.country + "-"+data.region + "-" + data.date+"-" + data.source
    }
    fun serializeDataToJson(data: CovidElasticData): String{
        val sw= StringWriter()
        mapper.writeValue(sw, data)
        sw.flush()
        return sw.toString()
    }

    /**
     * send whole JSOn to elastic search
     */
    fun sendToElastic(json: String, id: String){
        val request = IndexRequest(indexName)
        request.id(id)
        request.source(json, XContentType.JSON)

        logger.info("sending JSON:"+json)
        val indexResponse = client?.index(request, options)
        logger.info("elastic response=" + indexResponse)
    }

    fun deleteFromElastic(country: String, since: LocalDate){
        val today=LocalDate.now()

        val query="{\n" +
                "        'query':\n" +
                "        {\n" +
                "            'bool':{\n" +
                "            'must':[\n" +
                "            {\n" +
                "                'term':{\n" +
                "                'country':{\n" +
                "                'value':'"+country+"'\n" +
                "            }\n" +
                "            }\n" +
                "            }\n" +
                "            ],\n" +
                "            'filter': [\n" +
                "            {\n" +
                "                'range':{\n" +
                "                'date':{\n" +
                "                'gte':'"+since.toString()+"',\n" +
                "                'lte':'"+today.toString()+"',\n" +
                "                'format':'yyyy-MM-dd'\n" +
                "            }\n" +
                "            }\n" +
                "            }\n" +
                "            ]\n" +
                "        }\n" +
                "        }\n" +
                "    }"

        val q: DeleteByQueryRequest= DeleteByQueryRequest(indexName)
        q.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("country", country)).filter(QueryBuilders.rangeQuery("date").gte(since.toString()).lte(today.toString())))

        val result=client?.deleteByQuery(q, options)
        logger.info("deleted data for country "+country+" between "+since.toString()+" and today: "+result)
    }

}