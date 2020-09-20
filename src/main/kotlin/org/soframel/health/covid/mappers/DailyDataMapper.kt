package org.soframel.health.covid.mappers

import org.soframel.health.covid.model.CountryDailyData
import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.swiss.SwissCovidDailyData

interface DailyDataMapper<T : CountryDailyData> {

    fun map(d: T): CovidElasticData
    //fun map(l: List<T>): List<CovidElasticData>


    fun map(l: List<T>): List<CovidElasticData> {
        return l.map { d -> this.map(d) }
    }
}