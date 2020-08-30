package org.soframel.health.covid.mappers

import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.ElasticVictim
import org.soframel.health.covid.model.Gender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class FrenchDataElasticMapper: DailyDataMapper<FrenchCovidDailyData>{

    val POPULATION_FRANCE: Int=66524000

    override fun map(data: FrenchCovidDailyData): CovidElasticData{
        val edata=CovidElasticData()
        edata.date=data.date
        edata.source=data.sourceType
        edata.country="FR"
        if(data.nom!="France") {
            edata.region = data.nom
        }
        else{
            edata.region=""
        }
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