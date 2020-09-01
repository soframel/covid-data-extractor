package org.soframel.health.covid.client

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.soframel.health.covid.model.lux.LuxCovidDailyData
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.collections.ArrayList

@ApplicationScoped
class LuxCovidDataClient {

    val logger = Logger.getLogger(LuxCovidDataClient::class.qualifiedName)

    @Inject
    @ConfigProperty(name = "org.soframel.health.covid.client.LuxCovidDataURL")
    var covidDataURL: String?=null

    val httpClient: HttpClient=HttpClients.createDefault()

    fun fetchData(): InputStream? {
        logger.log(Level.FINE, "fetching data from "+covidDataURL)
                val httpGet = HttpGet(covidDataURL)
            val response1 = httpClient.execute(httpGet)

                logger.log(Level.INFO,  "response status="+response1.statusLine)
                val entity1 = response1.entity
                return entity1.content

    }


}