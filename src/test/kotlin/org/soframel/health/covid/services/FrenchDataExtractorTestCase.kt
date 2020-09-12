package org.soframel.health.covid.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FrenchDataExtractorTestCase: AbstractCovidDataExtractorTestCase() {

    var extractor=FrenchDataExtractor()

    @Test
    fun testRegionImport(){

        val result=this.loadJSON("/AllDataByDate-2020-08-20.json")
        Assertions.assertEquals(121, result.allFranceDataByDate.size)

        val ain=result.allFranceDataByDate[0]

        assertTrue(extractor.shouldDataBeImported(ain))
    }


    @Test
    fun testCountryImport(){

        val result=this.loadJSON("/AllDataByDate-2020-08-20.json")
        val countryResuts=result.allFranceDataByDate.filter{it.code.equals("FRA")}
        assertTrue(extractor.shouldDataBeImported(countryResuts[0]))
        assertFalse(extractor.shouldDataBeImported(countryResuts[1]))
    }
}