package org.soframel.health.covid.services

import org.soframel.health.covid.client.SwissCovidDataClient
import org.soframel.health.covid.elastic.ElasticDataAggregator
import org.soframel.health.covid.elastic.ElasticSender
import org.soframel.health.covid.mappers.SwissDataElasticMapper
import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.swiss.SwissCovidDailyData
import java.io.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    @Inject
    @field: Default
    lateinit var aggregator: ElasticDataAggregator

    fun extractAllData(){
        logger.info("extracting Swiss data from the start")
        this.extractDataSince(null)
    }

    fun extractDataSince(date: LocalDate?){
        logger.info("extracting swiss data since "+date)
        val inputStream=swissDataClient.fetchData()
        if(inputStream!=null) {
            val lines = readInputStream(inputStream)
            this.processDataSince(date, lines)
        }
    }

    /*
   like parseAllData but parses only last line
    */
    fun extractTodaysData(){
        logger.info("extracting swiss data for last day")
        val inputStream=swissDataClient.fetchData()
        if(inputStream!=null) {
            val lines = readInputStream(inputStream)
            val lastLine=lines.get(lines.size-1)
            val date=this.parseLine(lastLine).date
            logger.info("found last date in data="+date+", from a list of "+lines.size+" lines")
            this.processDataSince(date, lines)
        }
    }

    fun processDataSince(startDate: LocalDate?, lines: List<String>){
        var allDataForDate= mutableListOf<CovidElasticData>()
        var previousDate: LocalDate?=null
        for(line in lines){
            val data=this.parseLine(line)
            val date=data.date

            if(previousDate==null)
                previousDate=date

            if(startDate==null || date.equals(startDate) || date.isAfter(startDate)) {
                val edata = mapper.map(data)

                elasticSender.serializeAndSend(edata)

                if(date.equals(previousDate)) {
                    allDataForDate.add(edata)
                }
                else{
                    //then country-wide data
                    elasticSender.serializeAndSend(aggregator.aggregate(allDataForDate))
                    allDataForDate= mutableListOf<CovidElasticData>()
                    previousDate=startDate
                }
            }
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

    fun readInputStream(inputStream: InputStream): List<String>{
        val reader = BufferedReader(inputStream.reader())
        val list= mutableListOf<String>()
        //ignore first line
        reader.readLine()
        try {
            var line = reader.readLine()
            while (line != null) {
                list.add(line)
                line = reader.readLine()
            }
        } finally {
            reader.close()
        }
        return list
    }
}