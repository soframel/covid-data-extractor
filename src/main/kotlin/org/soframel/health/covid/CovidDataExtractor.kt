package org.soframel.health.covid

import io.quarkus.runtime.QuarkusApplication
import org.soframel.health.covid.services.FrenchDataExtractor
import org.soframel.health.covid.services.LuxDataExtractor
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

    val startOfPandemy=LocalDate.of(2020, 3, 1)

    override fun run(args: Array<String>): Int {
        if(args.size>0 && args[0].equals("init")){
            if(args.size>1) {
                val start = args[1]
                val startDate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE)

                logger.info("initialization - extracting data since " + startDate)

                if(args.size>2){
                    val country=args[2]
                    if(country.toUpperCase().equals("FR")){
                        frenchDataExtractor.extractFrenchDataSinceStartDate(startDate)
                    }
                    else if(country.toUpperCase().equals("LU")){
                        luxDataExtractor.extractDataSince(startDate)
                    }
                }
                else {
                    //extract all since startDate
                    frenchDataExtractor.extractFrenchDataSinceStartDate(startDate)
                    luxDataExtractor.extractDataSince(startDate)
                }
            }
            else{
                logger.info("initialization - extracting all data")
                frenchDataExtractor.extractFrenchDataSinceStartDate(startOfPandemy)

                luxDataExtractor.extractAllData()
            }
        }
        else {
            luxDataExtractor.extractTodaysData()
        }
        //extract french today's data in all cases: not in initialization
        frenchDataExtractor.extractFrenchDataForToday()

        return 0;
    }
}