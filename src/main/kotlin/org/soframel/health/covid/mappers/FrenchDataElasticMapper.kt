package org.soframel.health.covid.mappers

import org.eclipse.microprofile.rest.client.inject.RestClient
import org.soframel.health.covid.client.CoronavirusAPIFrance
import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.ElasticVictim
import org.soframel.health.covid.model.Gender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class FrenchDataElasticMapper: DailyDataMapper<FrenchCovidDailyData>{

    val POPULATION_FRANCE: Long=66524000

    var departmentPopulation=FrenchDepartmentPopulation()


    override fun map(data: FrenchCovidDailyData): CovidElasticData{
        val edata=CovidElasticData()
        edata.date=data.date
        edata.source=data.sourceType
        edata.country="FR"
        if(data.nom.equals("Monde")){
            edata.country="World"
            edata.region=""
        }
        else if(data.nom.equals("France")) {
            edata.region = ""
        }
        else {
            edata.region = data.nom
        }

        edata.totalCases=data.casConfirmes+data.casConfirmesEhpad
        edata.totalDeaths=data.deces+data.decesEhpad
        edata.currentlyHospitalized=data.hospitalisation+data.hospitalise+data.hospitalises+data.hospitalisesAuxUrgences+data.hospitalisesConventionnelle+data.hospitalisesReadaptation
        edata.currentlyInReanimation=data.reanimation+data.reanimations
        edata.totalCured=data.gueris
        edata.newHospitalisations=data.nouvellesHospitalisations
        edata.newReanimations=data.nouvellesReanimations
        edata.victimes=data.victimes.map{ v -> ElasticVictim(v.age, Gender.valueOf(v.sexe.toString()), v.date) }

        this.computeAdditionalValuesFromPopulation(data.code, edata)

        return edata
    }

    /**
     * compute additional values based on departement/region populations.
     *
     */
    fun computeAdditionalValuesFromPopulation(code: String, edata: CovidElasticData){
        var population=0L
        if(code.equals("FRA")){
            population=POPULATION_FRANCE
        }
        else {
            population = departmentPopulation.getDepartmentPopulation(code)
        }

            if(population>0) {
                edata.computeAdditionalValues(population)
            }
    }

}