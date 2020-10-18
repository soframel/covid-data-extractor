package org.soframel.health.covid

import io.quarkus.runtime.QuarkusApplication
import org.soframel.health.covid.services.FrenchDataExtractor
import org.soframel.health.covid.services.LuxDataExtractor
import org.soframel.health.covid.services.SwissDataExtractor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.logging.Logger
import javax.enterprise.inject.Default
import javax.inject.Inject

@io.quarkus.runtime.annotations.QuarkusMain
class CovidDataExtractor: QuarkusApplication {

    val logger = Logger.getLogger(CovidDataExtractor::class.qualifiedName)

    @Inject
    @field: Default
    lateinit var frenchDataExtractor: FrenchDataExtractor

    @Inject
    @field: Default
    lateinit var luxDataExtractor: LuxDataExtractor

    @Inject
    @field: Default
    lateinit var swissDataExtractor: SwissDataExtractor

    val startOfPandemy=LocalDate.of(2020, 3, 1)

    override fun run(args: Array<String>): Int {

        //bulk cases: intializing or replacing data
        if(args.size>0 && (args[0].equals("init") || args[0].equals("replace"))){

            var startDate=startOfPandemy
            if(args.size>1) {
                val start = args[1]
                startDate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE)
            }
            var country: String=""
            if(args.size>2){
                val country=args[2].toUpperCase()
            }

            var init=(args[0]).equals("init")

            logger.info("initializating/replacing - extracting data since " + startDate+", for "+country)

                when (country) {
                    "FR" -> {
                        if (init) frenchDataExtractor.extractFrenchDataSinceStartDate(startDate) else frenchDataExtractor.deleteAndExtractAllDataSince(startDate)
                        //extract french today's data in all cases: not in initialization
                        frenchDataExtractor.extractFrenchDataForToday()
                    }
                    "LU" -> if (init) luxDataExtractor.extractDataSince(startDate) else luxDataExtractor.deleteAndExtractAllDataSince(startDate)
                    "CH" -> if (init) swissDataExtractor.extractDataSince(startDate) else swissDataExtractor.deleteAndExtractAllDataSince(startDate)
                    else -> {
                        if (init) {
                            logger.info("initializing - extracting all data since "+startDate)
                            //extract all since startDate
                            frenchDataExtractor.extractFrenchDataSinceStartDate(startDate)
                            //extract french today's data in all cases: not in initialization
                            frenchDataExtractor.extractFrenchDataForToday()
                            luxDataExtractor.extractDataSince(startDate)
                            swissDataExtractor.extractDataSince(startDate)
                        }
                        else{
                            logger.info("replacing - extracting all data since "+startDate)
                            frenchDataExtractor.deleteAndExtractAllDataSince(startDate)
                            //extract french today's data in all cases: not in initialization
                            frenchDataExtractor.extractFrenchDataForToday()
                            luxDataExtractor.deleteAndExtractAllDataSince(startDate)
                            swissDataExtractor.deleteAndExtractAllDataSince(startDate)
                        }
                    }
                }

        }
        else { //normal case, processing the day's data
            val today=LocalDate.now()
            //on sunday get again data for all days
            if(today.dayOfWeek==DayOfWeek.SUNDAY){
                val lastSunday=today.minusDays(7)
                frenchDataExtractor.deleteAndExtractAllDataSince(lastSunday)
                luxDataExtractor.deleteAndExtractAllDataSince(lastSunday)
                swissDataExtractor.deleteAndExtractAllDataSince(lastSunday)
            }
            else {
                frenchDataExtractor.extractFrenchDataForToday()
                luxDataExtractor.extractTodaysData()
                swissDataExtractor.extractTodaysData()
            }
        }


        return 0;
    }
}