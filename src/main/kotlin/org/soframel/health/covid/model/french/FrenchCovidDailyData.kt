package org.soframel.health.covid.model.french

import org.soframel.health.covid.model.CountryDailyData
import java.time.LocalDate
import java.util.Date

class FrenchCovidDailyData : CountryDailyData {
    var date: LocalDate =LocalDate.now()
    var source: Source = Source()
    var sourceType: String=""
    var nom: String=""
    var code: String=""

    var casConfirmes: Long=0
    var deces: Long=0
    var decesEhpad: Long=0
    var hospitalises: Long=0
    var hospitalise: Long=0
    var hospitalisation: Long=0
    var reanimation: Long=0
    var reanimations: Long=0
    var gueris: Long=0
    var casConfirmesEhpad: Long=0
    var casEhpad:Long=0
    var casPossiblesEhpad:Long=0

    var nouvellesHospitalisations: Long=0
    var nouvellesReanimations: Long=0
    var hospitalisesReadaptation:Long=0
    var hospitalisesAuxUrgences:Long=0
    var hospitalisesConventionnelle:Long=0
    var paysTouches: Long=0
    var victimes= ArrayList<Victime>()
    var capaciteLitsReanimation: Long=0
    var capaciteTotaleLitsDisponibles:Long=0
    var capaciteLitsSoinsContinus:Long=0
    var capaciteLitsSoinsIntensifs:Long=0
    var capaciteReanimation:Long=0
    var depistes:Long=0

    override fun toString(): String {
        return "MinistereSanteData(date=$date, source=$source, sourceType=$sourceType, nom='$nom', code='$code', casConfirmes=$casConfirmes, deces=$deces, decesEhpad=$decesEhpad, hospitalises=$hospitalises, reanimation=$reanimation, gueris=$gueris, casConfirmesEhpad=$casConfirmesEhpad, nouvellesHospitalisations=$nouvellesHospitalisations, nouvellesReanimations=$nouvellesReanimations)"
    }
}