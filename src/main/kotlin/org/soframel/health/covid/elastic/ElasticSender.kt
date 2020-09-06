package org.soframel.health.covid.elastic


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.soframel.health.covid.model.CountryDailyData
import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import java.io.StringWriter
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
        logger.info("building client with scheme $elasticScheme, hostname $elasticHostname, port $elasticPort and username $elasticUsername")
        client= RestHighLevelClient(org.elasticsearch.client.RestClient.builder(org.apache.http.HttpHost(elasticHostname, java.lang.Integer.parseInt(elasticPort), elasticScheme)))

        elasticPassword=System.getProperty(ELASTIC_PASSWORD_PROPNAME)
        logger.info("******************* "+elasticPassword)
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

}