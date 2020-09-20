package org.soframel.health.covid.mappers

import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.ElasticVictim
import org.soframel.health.covid.model.Gender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import org.soframel.health.covid.model.lux.LuxCovidDailyData
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class LuxembourgDataElasticMapper: DailyDataMapper<LuxCovidDailyData>{

    val POPULATION_LUXEMBOURG: Long=613894
    val SOURCETYPE="ministere-sante"

    override fun map(data: LuxCovidDailyData): CovidElasticData{
        val edata=CovidElasticData()
        edata.date=data.date
        edata.source=SOURCETYPE
        edata.country="LU"
        edata.region=""
        edata.totalCases=data.totalInfections.toLong()
        edata.totalDeaths= data.nombreDeMorts.toLong()
        edata.currentlyHospitalized=data.soinsNormaux.toLong()+data.soinsIntensifs.toLong()
        edata.currentlyInReanimation=data.soinsIntensifs.toLong()

        edata.totalTested=data.nbTestsTotal.toLong()
        edata.newPositiveTests=data.nbPersonnesTesteesPositifs.toLong()

        edata.computeAdditionalValues(POPULATION_LUXEMBOURG)
        return edata
    }

}