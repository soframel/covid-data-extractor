package org.soframel.health.covid.mappers

import org.soframel.health.covid.model.CountryDailyData
import org.soframel.health.covid.model.CovidElasticData

interface DailyDataMapper<T : CountryDailyData> {

    fun map(d: T): CovidElasticData
}