package org.soframel.health.covid.model.french

import org.soframel.health.covid.model.CountryDailyData
import java.time.LocalDate
import java.util.Date

class FrenchCovidDailyData : CountryDailyData {
    val date: LocalDate =LocalDate.now()
    val source: Source = Source()
    val sourceType: String=""
    val nom: String=""
    val code: String=""

    val casConfirmes: Long=0
    val deces: Long=0
    val decesEhpad: Long=0
    val hospitalises: Long=0
    val hospitalise: Long=0
    val hospitalisation: Long=0
    val reanimation: Long=0
    val reanimations: Long=0
    val gueris: Long=0
    val casConfirmesEhpad: Long=0
    val casEhpad:Long=0
    val casPossiblesEhpad:Long=0

    val nouvellesHospitalisations: Long=0
    val nouvellesReanimations: Long=0
    val hospitalisesReadaptation:Long=0
    val hospitalisesAuxUrgences:Long=0
    val hospitalisesConventionnelle:Long=0
    val paysTouches: Long=0
    val victimes= ArrayList<Victime>()
    val capaciteLitsReanimation: Long=0
    val capaciteTotaleLitsDisponibles:Long=0
    val capaciteLitsSoinsContinus:Long=0
    val capaciteLitsSoinsIntensifs:Long=0
    val capaciteReanimation:Long=0
    val depistes:Long=0

    override fun toString(): String {
        return "MinistereSanteData(date=$date, source=$source, sourceType=$sourceType, nom='$nom', code='$code', casConfirmes=$casConfirmes, deces=$deces, decesEhpad=$decesEhpad, hospitalises=$hospitalises, reanimation=$reanimation, gueris=$gueris, casConfirmesEhpad=$casConfirmesEhpad, nouvellesHospitalisations=$nouvellesHospitalisations, nouvellesReanimations=$nouvellesReanimations)"
    }
}