package org.soframel.health.covid.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.soframel.health.covid.mappers.SwissDataElasticMapper
import java.time.LocalDate

class SwissDataExtractorTestCase: AbstractCovidDataExtractorTestCase() {

    var extractor=SwissDataExtractor()

    @Test
    fun testParseLine(){

        val result=extractor.parseLine("2020-03-07,08:00,ZG,,3,1,2,3,4,5,6,https://www.zg.ch/behoerden/gesundheitsdirektion/statistikfachstelle/themen/gesundheit/corona,5,8")

        assertEquals(LocalDate.of(2020,3,7), result.date)
        assertEquals("ZG", result.canton)
        assertEquals(0, result.totalTests)
        assertEquals(3, result.totalConfirmed)
        assertEquals(1, result.newHospitalizations)
        assertEquals(2, result.currentHospitalizations)
        assertEquals(3, result.currentICU)
        assertEquals(6, result.totalDeaths)
        assertEquals("https://www.zg.ch/behoerden/gesundheitsdirektion/statistikfachstelle/themen/gesundheit/corona", result.source)
        assertEquals(5, result.currentIsolated)
        assertEquals(8, result.currentQuarantine)

        
    }

}