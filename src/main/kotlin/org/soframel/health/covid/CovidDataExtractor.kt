package org.soframel.health.covid

import io.quarkus.runtime.QuarkusApplication
import org.soframel.health.covid.service.FrenchDataExtractor
import org.soframel.health.covid.service.LuxDataExtractor
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
        /*if(args.size>0 && args[0].equals("init")){
            val startOfPandemy= LocalDate.of(2020, 3, 1)
            frenchDataExtractor.extractFrenchDataSinceStartDate(startOfPandemy)
        }
        frenchDataExtractor.extractFrenchDataForToday()*/

        luxDataExtractor.getAllData()

        return 0;
    }
}