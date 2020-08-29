package org.soframel.health.covid.service

import org.soframel.health.covid.client.LuxCovidDataClient
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

    fun getAllData(): List<LuxCovidDailyData>{
        val inputStream=luxDataClient.fetchData()
        val result=ArrayList<LuxCovidDailyData>()
        if(inputStream!=null) {
            val buffered = BufferedReader(InputStreamReader(inputStream))
            //ignore first line = headers
            buffered.readLine()
            do {
                val line = buffered.readLine();
                logger.log(Level.FINE, "parsing line "+line)
                if (line != null) {
                    result.add(this.parseLine(line))
                }
            } while (line != null)
        }
        else{
            logger.log(Level.WARNING, "no inputStream for data")
        }
       return result
    }

    /*
    like parseAllData but parses only last line
     */
    fun getTodaysData(){
        //TODO
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
        data.nbPersonnesTestéesPositifs=parseInt(tokenizer.nextToken())
        data.nbTestsTotal=parseInt(tokenizer.nextToken())

        logger.info("parsed "+data)

        return data
    }

    fun parseInt(s: String): Int{
        if(s==null || s.equals("-")){
            return 0
        }
        else{
            return Integer.parseInt(s)
        }
    }
}