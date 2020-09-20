package org.soframel.health.covid.model

import java.time.LocalDate

class CovidElasticData {

    var date: LocalDate = LocalDate.now()
    var source: String = ""
    var country: String = ""
    var region: String = ""
    var totalCases: Long = 0
    var totalDeaths: Long = 0
    var currentlyHospitalized: Long = 0
    var currentlyInReanimation: Long = 0
    var totalCured: Long = 0
    var newHospitalisations: Long = 0
    var newReanimations: Long = 0
    var victimes: List<ElasticVictim> = ArrayList<ElasticVictim>()

    //tests
    var totalTested: Long = 0
    var newPositiveTests: Long = 0

    //compulted values
    var totalCasesPer100kInhabitants: Long = 0
    var totalDeathsPer100kInhabitants: Long = 0
    var reanimationPer100kInhabitants: Long = 0

    var population: Long = 0

    fun computeAdditionalValues(population: Long) {
        this.population = population

        if (totalCases > 0) {
            this.totalCasesPer100kInhabitants = this.totalCases * 100000 / population
        }
        if (totalDeaths > 0) {
            this.totalDeathsPer100kInhabitants = this.totalDeaths * 100000 / population
        }
        if (currentlyInReanimation > 0) {
            this.reanimationPer100kInhabitants = this.currentlyInReanimation * 100000 / population
        }
    }
}