package org.soframel.health.covid.model

class ElasticVictim(age: Int=0, gender: Gender=Gender.undefined, date: String=""){


    //val date: LocalDate =LocalDate.now()
    //some date have another format, 29-03-20, so parsing as string for now

}

enum class Gender{
    undefined, homme, femme
}