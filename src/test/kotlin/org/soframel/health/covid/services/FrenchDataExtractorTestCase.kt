package org.soframel.health.covid.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.soframel.health.covid.model.french.FrenchCovidDailyData

class FrenchDataExtractorTestCase: AbstractCovidDataExtractorTestCase() {

    var extractor=FrenchDataExtractor()

    /*@Test
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
    }*/

    @Test
    fun testRemoveDoubles(){
        var dataRegion1=FrenchCovidDailyData()
        dataRegion1.code="REG1"
        dataRegion1.sourceType="covid-19"
        dataRegion1.casConfirmes=42

        var dataRegion1b=FrenchCovidDailyData()
        dataRegion1b.code="REG1"
        dataRegion1b.sourceType="sante-publique-france-data"
        dataRegion1b.casConfirmes=43

        var dataRegion2=FrenchCovidDailyData()
        dataRegion2.code="REG2"
        dataRegion2.sourceType="covid-19"
        dataRegion2.casConfirmes=93

        var dataFrance=FrenchCovidDailyData()
        dataFrance.code="FRA"
        dataFrance.sourceType="sante-publique-france-data"
        dataFrance.casConfirmes=1024

        var dataFranceb=FrenchCovidDailyData()
        dataFranceb.code="FRA"
        dataFranceb.sourceType="covid-19"
        dataFranceb.casConfirmes=1025

        val list=listOf(dataRegion1, dataRegion1b, dataRegion2, dataFrance, dataFranceb)
        val newList=extractor.removeDoubles(list)
        assertEquals(3, newList.size)
        for(data in newList){
            if(data.code.equals("REG1")){
                assertEquals(43, data.casConfirmes)
            }
            else if(data.code.equals("REG2")){
                assertEquals(93, data.casConfirmes)
            }
            else if(data.code.equals("FRA")){
                assertEquals(1024, data.casConfirmes)
            }
        }
    }
}