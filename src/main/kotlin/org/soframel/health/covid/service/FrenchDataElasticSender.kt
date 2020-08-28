package org.soframel.health.covid.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.ElasticVictim
import org.soframel.health.covid.model.Gender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import java.io.StringWriter
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject


@ApplicationScoped
class FrenchDataElasticSender{

    val POPULATION_FRANCE: Int=66524000

    @Inject
    @field: Default
    lateinit var elasticSender: ElasticSender

    @Inject
    @field: Default
    lateinit var dataMapper: FrenchDataElasticMapper

    val mapper: ObjectMapper
    init{
        mapper= ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    fun sendDailyDataToElastic(data: FrenchCovidDailyData){
        val edata=dataMapper.transformFrenchDataToElastic(data)
        val sw= StringWriter()
        mapper.writeValue(sw, edata)
        sw.flush()
        val json=sw.toString()

        elasticSender.sendToElastic(json, data.sourceType + "-" + data.source + "-" + data.date)
    }

}