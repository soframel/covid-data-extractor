package org.soframel.health.covid.model.french

import com.fasterxml.jackson.annotation.JsonProperty

//import javax.json.bind.annotation.JsonbProperty

class FrenchResult constructor (){
    @JsonProperty("FranceGlobalLiveData")
    val franceGlobalLiveData: ArrayList<FrenchCovidDailyData> =  ArrayList<FrenchCovidDailyData>();

    val allFranceDataByDate: ArrayList<FrenchCovidDailyData> =  ArrayList<FrenchCovidDailyData>();

    val allLiveFranceData: ArrayList<FrenchCovidDailyData> =  ArrayList<FrenchCovidDailyData>();
}