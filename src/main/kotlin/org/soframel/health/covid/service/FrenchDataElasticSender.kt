package org.soframel.health.covid.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.ElasticVictim
import org.soframel.health.covid.model.Gender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import org.soframel.health.covid.model.french.Victime
import java.io.StringWriter
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject

@ApplicationScoped
class FrenchDataElasticSender{

    @Inject
    @field: Default
    lateinit var elasticSender: ElasticSender

    fun sendDailyDataToElastic(data: FrenchCovidDailyData){
        val edata=this.transformFrenchDataToElastic(data)
        val objectMapper = ObjectMapper()
        val sw= StringWriter()
        objectMapper.writeValue(sw, edata)
        sw.flush()
        val json=sw.toString()

        elasticSender.sendToElastic(json, data.sourceType + "-" + data.source + "-" + data.date)
    }

    private fun transformFrenchDataToElastic(data: FrenchCovidDailyData): CovidElasticData{
        val edata=CovidElasticData()
        edata.date=data.date
        edata.source=data.sourceType
        edata.country="FR"
        edata.region=data.nom
        edata.totalCases=data.casConfirmes
        edata.totalDeaths=data.deces
        edata.currentlyHospitalized=data.hospitalisation+data.hospitalise+data.hospitalises+data.hospitalisesAuxUrgences+data.hospitalisesConventionnelle+data.hospitalisesReadaptation
        edata.currentlyInReanimation=data.reanimation+data.reanimations
        edata.totalCured=data.gueris
        edata.newHospitalisations=data.nouvellesHospitalisations
        edata.newReanimations=data.nouvellesReanimations
        edata.victimes=data.victimes.map{  v -> ElasticVictim(v.age, Gender.valueOf(v.sexe.toString()), v.date) }
        return edata
    }
}