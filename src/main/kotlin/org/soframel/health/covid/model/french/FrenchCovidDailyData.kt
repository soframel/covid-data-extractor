package org.soframel.health.covid.model.french

import java.time.LocalDate
import java.util.Date

class FrenchCovidDailyData constructor( ) {
    val date: LocalDate =LocalDate.now()
    val source: Source = Source()
    val sourceType: String=""
    val nom: String=""
    val code: String=""

    val casConfirmes: Int=0
    val deces: Int=0
    val decesEhpad: Int=0
    val hospitalises: Int=0
    val hospitalise: Int=0
    val hospitalisation: Int=0
    val reanimation: Int=0
    val reanimations: Int=0
    val gueris: Int=0
    val casConfirmesEhpad: Int=0
    val casEhpad:Int=0
    val casPossiblesEhpad:Int=0

    val nouvellesHospitalisations: Int=0
    val nouvellesReanimations: Int=0
    val hospitalisesReadaptation:Int=0
    val hospitalisesAuxUrgences:Int=0
    val hospitalisesConventionnelle:Int=0
    val paysTouches: Int=0
    val victimes= ArrayList<Victime>()
    val capaciteLitsReanimation: Int=0
    val capaciteTotaleLitsDisponibles:Int=0
    val capaciteLitsSoinsContinus:Int=0
    val capaciteLitsSoinsIntensifs:Int=0
    val capaciteReanimation:Int=0
    val depistes:Int=0

    override fun toString(): String {
        return "MinistereSanteData(date=$date, source=$source, sourceType=$sourceType, nom='$nom', code='$code', casConfirmes=$casConfirmes, deces=$deces, decesEhpad=$decesEhpad, hospitalises=$hospitalises, reanimation=$reanimation, gueris=$gueris, casConfirmesEhpad=$casConfirmesEhpad, nouvellesHospitalisations=$nouvellesHospitalisations, nouvellesReanimations=$nouvellesReanimations)"
    }
}