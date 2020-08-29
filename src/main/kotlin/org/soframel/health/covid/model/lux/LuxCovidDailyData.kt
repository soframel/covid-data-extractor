package org.soframel.health.covid.model.lux

import java.time.LocalDate

class LuxCovidDailyData{

    var date: LocalDate=LocalDate.now()
    var soinsNormaux: Int=0
    var soinsIntensifs: Int=0
    var soinsIntensifsSansGE: Int=0
    var nombreDeMorts: Int=0
    var totalPatientsSortisDhopital: Int=0
    var totalInfections: Int=0
    var nbPersonnesTestéesPositifs: Int=0
    var nbTestsTotal: Int=0
    override fun toString(): String {
        return "LuxCovidDailyData(date=$date, soinsNormaux=$soinsNormaux, soinsIntensifs=$soinsIntensifs, soinsIntensifsSansGE=$soinsIntensifsSansGE, nombreDeMorts=$nombreDeMorts, totalPatientsSortisDhopital=$totalPatientsSortisDhopital, totalInfections=$totalInfections, nbPersonnesTestéesPositifs=$nbPersonnesTestéesPositifs, nbTestsTotal=$nbTestsTotal)"
    }


}