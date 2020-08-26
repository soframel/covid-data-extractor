package org.soframel.health.covid.model

import com.fasterxml.jackson.annotation.JsonProperty

//import javax.json.bind.annotation.JsonbProperty

class FrenchResult constructor (){
    //@JsonbProperty("FranceGlobalLiveData")
    @JsonProperty("FranceGlobalLiveData")
    val franceGlobalLiveData: ArrayList<CovidDailyData> =  ArrayList<CovidDailyData>();

    val allFranceDataByDate: ArrayList<CovidDailyData> =  ArrayList<CovidDailyData>();
}