package org.soframel.health.covid.services

import io.quarkus.test.Mock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.soframel.health.covid.elastic.ElasticDataAggregator
import org.soframel.health.covid.elastic.ElasticSender
import org.soframel.health.covid.mappers.SwissDataElasticMapper
import java.time.LocalDate

class SwissDataExtractorTestCase: AbstractCovidDataExtractorTestCase() {

    var extractor=SwissDataExtractor()

    @InjectMocks
    lateinit var sender: ElasticSender

    @BeforeAll
    fun init() {
        MockitoAnnotations.initMocks(this);
        //Mockito.`when`(sender.serializeAndSend())
    }

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

    @Test
    fun testProcessDataSince(){

        val lines=listOf(
        "2020-04-25,13:00,GL,,120,,3,,,,7,https://www.gl.ch/public/upload/assets/30317/COVID-19_Fallzahlen_Kanton_Glarus.xlsx,,\n",
                "2020-04-25,14:30,ZH,,3375,,77,,38,,115,https://www.zh.ch/de/gesundheit/coronavirus.html,,\n",
                "2020-04-26,,BL,,816,,21,0,0,724,30,https://www.baselland.ch/politik-und-behorden/direktionen/volkswirtschafts-und-gesundheitsdirektion/amt-fur-gesundheit/medizinische-dienste/kantonsarztlicher-dienst/aktuelles/covid-19-faelle-kanton-basel-landschaft,,\n",
                "2020-04-26,,FL,,82,,,,,79,1,https://www.llv.li/files/ag/aktuelle-fallzahlen.pdf,,\n")

        extractor.processDataSince(LocalDate.of(2020,4,25), lines)

        Mockito.

    }
}