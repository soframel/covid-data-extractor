package org.soframel.health.covid.services

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.soframel.health.covid.client.LuxCovidDataClient
import org.soframel.health.covid.elastic.ElasticSender
import org.soframel.health.covid.mappers.LuxembourgDataElasticMapper
import org.soframel.health.covid.model.lux.LuxCovidDailyData
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import kotlin.collections.ArrayList

@ApplicationScoped
class LuxDataExtractor {

    val logger = Logger.getLogger(LuxDataExtractor::class.qualifiedName)

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject
    @field: Default
    lateinit var luxDataClient: LuxCovidDataClient

    @Inject
    @field: Default
    lateinit var mapper: LuxembourgDataElasticMapper

    @Inject
    @field: Default
    lateinit var elasticSender: ElasticSender

    fun extractAllData(){
        logger.info("extracting luxembourg data")
        val inputStream=luxDataClient.fetchData()

        if(inputStream!=null) {
            val buffered = BufferedReader(InputStreamReader(inputStream))
            //ignore first line = headers
            buffered.readLine()
            do {
                val line = buffered.readLine();
                logger.log(Level.FINE, "parsing line "+line)
                if (line != null) {
                    val data=this.parseLine(line)
                    val edata=mapper.map(data)
                    elasticSender.serializeAndSend(edata)
                }
            } while (line != null)
        }
        else{
            logger.log(Level.WARNING, "no inputStream for data")
        }
    }

    fun extractDataSince(date: LocalDate){
        logger.info("extracting luxembourg data since "+date)
        val inputStream=luxDataClient.fetchData()

        if(inputStream!=null) {
            val buffered = BufferedReader(InputStreamReader(inputStream))
            //ignore first line = headers
            buffered.readLine()
            do {
                val line = buffered.readLine();
                logger.log(Level.FINE, "parsing line "+line)
                if (line != null) {
                    val data=this.parseLine(line)
                    if(data.date.equals(date) || data.date.isAfter(date)) {
                        val edata = mapper.map(data)
                        elasticSender.serializeAndSend(edata)
                    }
                }
            } while (line != null)
        }
        else{
            logger.log(Level.WARNING, "no inputStream for data")
        }
    }

    /*
    like parseAllData but parses only last line
     */
    fun extractTodaysData(){
        logger.info("extracting luxembourg's data for last day")
        val inputStream=luxDataClient.fetchData()

        if(inputStream!=null) {
            val buffered = BufferedReader(InputStreamReader(inputStream))
            //ignore first line = headers
            buffered.readLine()
            var previousLine=""
            //TODO: find a more efficient way to read last file's line !
            do {
                val line = buffered.readLine();
                if(line!=null){
                    previousLine=line
                }
            } while (line != null)
            logger.log(Level.FINE, "reading last line "+previousLine)
            if (previousLine != null) {
                val data=this.parseLine(previousLine)
                val edata=mapper.map(data)
                elasticSender.serializeAndSend(edata)
            }
        }
        else{
            logger.log(Level.WARNING, "no inputStream for data")
        }
    }

    fun deleteAndExtractAllDataSince(date: LocalDate){
        logger.info("deleting and re-extracting data for last week")
        elasticSender.deleteFromElastic("LU", date)
        this.extractDataSince(date)
    }

    fun parseLine(line: String): LuxCovidDailyData{
        val data=LuxCovidDailyData()
        val tokenizer=StringTokenizer(line, ",")
        val dateString=tokenizer.nextToken()
        data.date= LocalDate.parse(dateString, dateFormatter)
        data.soinsNormaux=parseInt(tokenizer.nextToken())
        data.soinsIntensifs=parseInt(tokenizer.nextToken())
        data.soinsIntensifsSansGE=parseInt(tokenizer.nextToken())
        data.nombreDeMorts=parseInt(tokenizer.nextToken())
        data.totalPatientsSortisDhopital=parseInt(tokenizer.nextToken())
        data.totalInfections=parseInt(tokenizer.nextToken())
        data.nbPersonnesTesteesPositifs=parseInt(tokenizer.nextToken())
        data.nbTestsTotal=parseInt(tokenizer.nextToken())

        logger.info("parsed "+data)

        return data
    }

    fun parseInt(s: String): Int{
        if(s==null || s.equals("-")){
            return 0
        }
        else{
            //use Double, because its valueOf method supports notation with exponents
            return java.lang.Double.valueOf(s).toInt()
        }
    }
}