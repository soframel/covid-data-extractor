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

class FrenchDataElasticMapperTestCase: AbstractCovidDataExtractorTestCase() {

    var mapper= FrenchDataElasticMapper()

    @Test
    fun testRegionData(){
        val result=this.loadJSON("/AllDataByDate-2020-08-20.json")
        assertEquals(121, result.allFranceDataByDate.size)

        val ain=result.allFranceDataByDate[0]
        //before mapping, just to be sure...
        assertEquals("DEP-01", ain.code)
        assertEquals("Ain", ain.nom)
        assertEquals(LocalDate.of(2020, 8,20), ain.date)
        assertEquals(25, ain.hospitalises)
        assertEquals(1, ain.reanimation)
        assertEquals(2, ain.nouvellesHospitalisations)
        assertEquals(0, ain.nouvellesReanimations)
        assertEquals(105, ain.deces)
        assertEquals(460, ain.gueris)
        assertEquals("sante-publique-france-data", ain.sourceType)

        val data=mapper.map(ain)
        assertEquals(LocalDate.of(2020, 8,20), data.date)
        assertEquals("FR", data.country)
        assertEquals(25, data.currentlyHospitalized)
        assertEquals(1, data.currentlyInReanimation)
        assertEquals(2, data.newHospitalisations)
        assertEquals(0, data.newReanimations)
        assertEquals("Ain", data.region)
        assertEquals("sante-publique-france-data", data.source)
        assertEquals(460, data.totalCured)
        assertEquals(105, data.totalDeaths)
    }

    @Test
    fun testFranceMinistereSanteData(){
        val result=this.loadJSON("/AllDataByDate-2020-08-20.json")
        assertEquals(121, result.allFranceDataByDate.size)

        val countryResuts=result.allFranceDataByDate.filter{it.code.equals("FRA")}
        assertEquals(2, countryResuts.size)
        val ministereSante=countryResuts[0]

       /*
          "date": "2020-08-20",
      "source": {
        "nom": "Ministère des Solidarités et de la Santé"
      },
      "sourceType": "ministere-sante",
      "casConfirmes": 229814,
      "deces": 19969,
      "decesEhpad": 10511,
      "hospitalises": 4748,
      "reanimation": 380,
      "gueris": 84642,
      "casConfirmesEhpad": 39930,
      "nouvellesHospitalisations": 149,
      "nouvellesReanimations": 28,
      "nom": "France",
      "code": "FRA"
        */

        val data=mapper.map(ministereSante)
        assertEquals(LocalDate.of(2020, 8,20), data.date)
        assertEquals("FR", data.country)
        assertEquals("", data.region)
        assertEquals(229814+39930, data.totalCases)
        assertEquals(19969+10511, data.totalDeaths)
        assertEquals(4748, data.currentlyHospitalized)
        assertEquals(380, data.currentlyInReanimation)
        assertEquals(84642, data.totalCured)

        assertEquals(149, data.newHospitalisations)
        assertEquals(28, data.newReanimations)

        assertEquals("ministere-sante", data.source)


    }

}