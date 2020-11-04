package org.soframel.health.covid.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LuxDataExtractorTestCase: AbstractCovidDataExtractorTestCase() {

    var extractor = LuxDataExtractor()

    @Test
    fun testParseLine(){

        val d=extractor.parseLine("24/02/2020;1;2;3;4;-;5;6;7;8")
        assertEquals(LocalDate.of(2020,2,24), d.date)
        assertEquals(1, d.soinsNormaux)
        assertEquals(2, d.soinsIntensifs)
        assertEquals(3,d.soinsIntensifsSansGE)
        assertEquals(4,d.nombreDeMorts)
        assertEquals(0, d.totalPatientsSortisDhopital)
        assertEquals(5, d.totalInfections)
        assertEquals(6,d.nbPersonnesTesteesPositifs)
        assertEquals(7,d.nbTestsTotal)
    }
}