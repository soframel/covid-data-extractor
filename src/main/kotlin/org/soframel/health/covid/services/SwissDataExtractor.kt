package org.soframel.health.covid.services

import org.soframel.health.covid.client.SwissCovidDataClient
import org.soframel.health.covid.elastic.ElasticSender
import org.soframel.health.covid.mappers.SwissDataElasticMapper
import org.soframel.health.covid.model.swiss.SwissCovidDailyData
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

@ApplicationScoped
class SwissDataExtractor {

    val logger = Logger.getLogger(SwissDataExtractor::class.qualifiedName)

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    @field: Default
    lateinit var swissDataClient: SwissCovidDataClient

    @Inject
    @field: Default
    lateinit var mapper: SwissDataElasticMapper

    @Inject
    @field: Default
    lateinit var elasticSender: ElasticSender

    fun extractAllData(){
        logger.info("extracting Swiss data")
        val inputStream=swissDataClient.fetchData()

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
        logger.info("extracting swiss data since "+date)
        val inputStream=swissDataClient.fetchData()

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
        logger.info("extracting french data for last day")
        val inputStream=swissDataClient.fetchData()

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

    fun parseLine(line: String): SwissCovidDailyData {
        val data=SwissCovidDailyData()
        val array=line.split(",")
        data.date= LocalDate.parse(array[0], dateFormatter)
        //do not keep time
        data.canton=array[2]
        data.totalTests=parseLong(array[3])
        data.totalConfirmed=parseLong(array[4])
        data.newHospitalizations=parseLong(array[5])
        data.currentHospitalizations=parseLong(array[6])
        data.currentICU=parseLong(array[7])
        //do not read ventilator
        //do not read released
        data.totalDeaths=parseLong(array[10])
        data.source=array[11]
        data.currentIsolated=parseLong(array[12])
        data.currentQuarantine=parseLong(array[13])

        logger.info("parsed "+data)

        return data
    }

    fun parseLong(entry: String): Long {
        if(entry!=null && !entry.equals("")){
            return entry.toLong()
        }
        else{
            return 0L
        }
    }
}