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
class FrenchDataElasticMapper{

    val POPULATION_FRANCE: Int=66524000

    public fun transformFrenchDataToElastic(data: FrenchCovidDailyData): CovidElasticData{
        val edata=CovidElasticData()
        edata.date=data.date
        edata.source=data.sourceType
        edata.country="FR"
        edata.region=data.nom
        edata.totalCases=data.casConfirmes+data.casConfirmesEhpad
        edata.totalDeaths=data.deces+data.decesEhpad
        edata.currentlyHospitalized=data.hospitalisation+data.hospitalise+data.hospitalises+data.hospitalisesAuxUrgences+data.hospitalisesConventionnelle+data.hospitalisesReadaptation
        edata.currentlyInReanimation=data.reanimation+data.reanimations
        edata.totalCured=data.gueris
        edata.newHospitalisations=data.nouvellesHospitalisations
        edata.newReanimations=data.nouvellesReanimations
        edata.victimes=data.victimes.map{ v -> ElasticVictim(v.age, Gender.valueOf(v.sexe.toString()), v.date) }

        edata.computeAdditionalValues(POPULATION_FRANCE)
        return edata
    }
}