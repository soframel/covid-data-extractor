package org.soframel.health.covid

import io.quarkus.runtime.QuarkusApplication
import org.soframel.health.covid.services.FrenchDataExtractor
import org.soframel.health.covid.services.LuxDataExtractor
import java.time.LocalDate
import javax.enterprise.inject.Default
import javax.inject.Inject

@io.quarkus.runtime.annotations.QuarkusMain
class CovidDataExtractor: QuarkusApplication {

    @Inject
    @field: Default
    lateinit var frenchDataExtractor: FrenchDataExtractor

    @Inject
    @field: Default
    lateinit var luxDataExtractor: LuxDataExtractor

    override fun run(args: Array<String>): Int {
        if(args.size>0 && args[0].equals("init")){
            val startOfPandemy= LocalDate.of(2020, 3, 1)
            frenchDataExtractor.extractFrenchDataSinceStartDate(startOfPandemy)

            luxDataExtractor.extractAllData()
        }
        else {
            luxDataExtractor.extractTodaysData()
        }
        //extract french today's data in all cases: not in initialization
        frenchDataExtractor.extractFrenchDataForToday()

        return 0;
    }
}