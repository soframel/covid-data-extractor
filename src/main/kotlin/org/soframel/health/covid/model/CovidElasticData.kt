package org.soframel.health.covid.model

import java.time.LocalDate

class CovidElasticData {

    var date: LocalDate=LocalDate.now()
    var source: String=""
    var country: String=""
    var region: String=""
    var totalCases: Int=0
    var totalDeaths: Int=0
    var currentlyHospitalized=0
    var currentlyInReanimation=0
    var totalCured=0
    var newHospitalisations=0
    var newReanimations=0
    var victimes: List<ElasticVictim> = ArrayList<ElasticVictim>()

}