package org.soframel.health.covid.model.french

class Victime{

    val age: Int=0;
    val sexe: Sexe = Sexe.undefined
    //val date: LocalDate =LocalDate.now()
    //some date have another format, 29-03-20, so parsing as string for now
    val date:String=""
}

enum class Sexe{
    undefined, homme, femme
}