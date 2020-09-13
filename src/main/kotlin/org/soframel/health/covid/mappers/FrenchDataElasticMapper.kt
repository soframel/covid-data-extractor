package org.soframel.health.covid.mappers

import org.soframel.health.covid.model.CovidElasticData
import org.soframel.health.covid.model.ElasticVictim
import org.soframel.health.covid.model.Gender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class FrenchDataElasticMapper: DailyDataMapper<FrenchCovidDailyData>{

    val POPULATION_FRANCE: Int=66524000
    //Régions
    val POPULATION_ILEDEFRANCE: Int=12210000
    val POPULATION_AUVERGNERHONEALPES: Int=7948287
    val POPULATION_PACA: Int=5059000

    //Départements
    val POPULATION_PARIS: Int=2187526
    val POPULATION_RHONE: Int=1882000
    val POPULATION_BOUCHESDURHONE: Int=2035000
    val POPULATION_VAUCLUSE: Int=563751


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
     * done for the regions below only:
    département Rhône: DEP-69
    région auvergne rhone alpes: REG-84
    région ile de france: REG-11
    paris: DEP-75
    region PACA: REG-93
    departement bouches-du-rhône: DEP-13
    departement vaucluse: DEP-84

     */
    fun computeAdditionalValuesFromPopulation(code: String, edata: CovidElasticData){
            var population=0
            when(code){
                "FRA" -> population=POPULATION_FRANCE
                "DEP-69" -> population=POPULATION_RHONE
                "DEP-75" -> population=POPULATION_PARIS
                "DEP-13" -> population=POPULATION_BOUCHESDURHONE
                "DEP-84" -> population=POPULATION_VAUCLUSE
                "REG-84" -> population=POPULATION_AUVERGNERHONEALPES
                "REG-11" -> population=POPULATION_ILEDEFRANCE
                "REG-93" -> population=POPULATION_PACA
            }

            if(population>0) {
                edata.computeAdditionalValues(population)
            }
    }

    override fun map(l: List<FrenchCovidDailyData>): List<CovidElasticData> {
        return l.map { d -> this.map(d) }
    }
}