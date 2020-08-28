package org.soframel.health.covid.service


import org.apache.http.HttpHost
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import java.util.*
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject

@ApplicationScoped
class ElasticSender {
    val logger = Logger.getLogger("ElasticSender")

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

    @Inject
    @ConfigProperty(name = "elastic.password")
    var elasticPassword: String?=null

    @Inject
    @ConfigProperty(name="elastic.covid.indexName")
    var indexName: String?=null

    var client: RestHighLevelClient?=null
    var options: RequestOptions?=null

    @PostConstruct
    fun postInjectInit(){
        logger.info("building client with scheme $elasticScheme, hostname $elasticHostname, port $elasticPort and username $elasticUsername")
        client= RestHighLevelClient(org.elasticsearch.client.RestClient.builder(org.apache.http.HttpHost(elasticHostname, java.lang.Integer.parseInt(elasticPort), elasticScheme)))
        val auth=elasticUsername+":"+elasticPassword
        val token= Base64.getEncoder().encodeToString(auth.toByteArray())
        val builder = RequestOptions.DEFAULT.toBuilder()
        builder.addHeader("Authorization", "Basic $token")
        options = builder.build()
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