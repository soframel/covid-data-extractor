package org.soframel.health.covid.mappers

import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.ElasticVictim
import org.soframel.health.covid.model.Gender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import org.soframel.health.covid.model.lux.LuxCovidDailyData
import org.soframel.health.covid.model.swiss.SwissCovidDailyData
import org.soframel.health.covid.services.SwissDataExtractor
import java.util.logging.Logger
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class SwissDataElasticMapper: DailyDataMapper<SwissCovidDailyData>{

    val logger = Logger.getLogger(SwissDataElasticMapper::class.qualifiedName)

    val POPULATION_SWISS: Long=8570000L
    //source: wikipedia
    val POPULATION_CANTONS: Map<String,Long> =mapOf(
        "ZH" to	1520968L,
        "BE" to	1034977L,
        "LU" to 409557L,
        "UR" to 36433L,
        "SZ" to 159165L,
        "OW" to 37841L,
        "NW" to 43223L,
        "GL" to 40403L,
        "ZG" to 126837L,
        "FR" to 318714L,
        "SO" to 273194L,
        "BS" to 200298L,
        "BL" to 289527L,
        "SH" to 81991L,
        "AR" to 55234L,
        "AI" to 16145L,
        "SG" to 507697L,
        "GR" to 198379L,
        "AG" to 678207L,
        "TG" to 276472L,
        "TI" to 353343L,
        "VD" to 800162L,
        "VS" to 343955L,
        "NE" to 176850L,
        "GE" to 499480L,
        "JU" to 73419L,
            "FL" to 38749L //Liechtenstein
        )
    //"CH" to 8544527L

    override fun map(data: SwissCovidDailyData): CovidElasticData{
        val edata=CovidElasticData()
        edata.date=data.date
        edata.source=data.source
        edata.country="CH"
        edata.region=data.canton

        edata.totalTested=data.totalTests
        edata.totalCases=data.totalConfirmed
        edata.newHospitalisations=data.newHospitalizations
        edata.currentlyHospitalized=data.currentHospitalizations
        edata.currentlyInReanimation=data.currentICU
        edata.totalDeaths=data.totalDeaths

        this.computeAdditionalValuesFromPopulation(data.canton, edata)
        return edata
    }

    fun computeAdditionalValuesFromPopulation(canton: String?, edata: CovidElasticData){
        var population: Long=0

        if(canton==null || canton.equals("")){
            population=POPULATION_SWISS
        }
        else{
            val pop=POPULATION_CANTONS.get(canton)
            logger.fine("population for $canton is $pop")
            if(pop!=null){
                population=pop
            }
        }

        if(population>0) {
            edata.computeAdditionalValues(population)
        }
    }
}