package org.soframel.health.covid.model.swiss

import org.soframel.health.covid.model.CountryDailyData
import java.time.LocalDate

class SwissCovidDailyData : CountryDailyData{

    var date: LocalDate=LocalDate.now()
    var canton: String=""
    var totalTests: Long=0
    var totalConfirmed: Long=0
    var newHospitalizations: Long=0
    var currentHospitalizations: Long=0
    var currentICU: Long=0
    var currentVentilator: Long=0
    var totalReleased: Long=0
    var totalDeaths: Long=0
    var source: String=""
    var currentIsolated: Long=0
    var currentQuarantine: Long=0
    override fun toString(): String {
        return "SwissCovidDailyData(date=$date, canton='$canton', totalTests=$totalTests, totalConfirmed=$totalConfirmed, newHospitalizations=$newHospitalizations, currentHospitalizations=$currentHospitalizations, currentICU=$currentICU, currentVentilator=$currentVentilator, totalReleased=$totalReleased, totalDeaths=$totalDeaths,currentIsolated=$currentIsolated, currentQuanrantine=$currentQuarantine)"
    }


}