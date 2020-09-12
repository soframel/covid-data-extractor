package org.soframel.health.covid.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.soframel.health.covid.mappers.FrenchDataElasticMapper
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import org.soframel.health.covid.model.french.FrenchResult
import java.io.InputStream
import java.time.LocalDate
import java.util.logging.Logger
import javax.enterprise.inject.Default
import javax.inject.Inject

abstract class AbstractCovidDataExtractorTestCase{

    val objectMapper=ObjectMapper()
    init{
        objectMapper.registerModule(JavaTimeModule())
    }


    fun loadJSON(filename: String): FrenchResult{
        return objectMapper.readValue(this.javaClass.getResourceAsStream(filename))
    }
}