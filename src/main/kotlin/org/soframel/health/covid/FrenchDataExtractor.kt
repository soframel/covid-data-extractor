package org.soframel.health.covid

import org.eclipse.microprofile.rest.client.inject.RestClient;
import javax.inject.Inject;
import io.quarkus.runtime.QuarkusApplication;
import org.soframel.health.covid.client.CoronavirusAPIFrance
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import org.soframel.health.covid.service.FrenchDataElasticSender
import java.time.LocalDate
import java.util.*
import javax.enterprise.inject.Default

@io.quarkus.runtime.annotations.QuarkusMain
class FrenchDataExtractor: QuarkusApplication {

	@Inject
	@field: RestClient
	lateinit var frenchClient: CoronavirusAPIFrance

	@Inject
	@field: Default
	lateinit var frenchDataElasticSender: FrenchDataElasticSender

	 override fun run(args: Array<String>): Int {
		//this.extractFrenchDataForToday();
		 val startOfPandemy=LocalDate.of(2020, 3, 1)
		 this.extractFrenchDataSinceStartDate(startOfPandemy)
		 this.extractFrenchDataForToday()

		 return 0;
	}
	
	fun extractFrenchDataForToday(): FrenchCovidDailyData?{
		val result=frenchClient.getTodaysData();
		val dataList=result.franceGlobalLiveData;
		println("loaded "+ dataList.size +" entries")
		if(dataList.size>=0){
			val data= this.findMinistereSanteData(dataList)
			if(data!=null)
				frenchDataElasticSender.sendDailyDataToElastic(data)
			return data
		}
		return null
	}

	fun findMinistereSanteData(list: List<FrenchCovidDailyData>): FrenchCovidDailyData?{
		var foundData: FrenchCovidDailyData?=null
		var it=list.iterator()
		while(it.hasNext() && foundData==null){
			val data=it.next()
			if("ministere-sante".equals(data.sourceType)){
				foundData=data
			}
		}
		return foundData
	}

	fun extractFrenchDataForGivenDay(day: LocalDate): FrenchCovidDailyData?{
		val result=frenchClient.getDataAtDate(day)
		val dataList=result.allFranceDataByDate;
		println("loaded "+ dataList.size +" entries for day "+day)
		if(dataList.size>0){
			val data= this.findMinistereSanteData(dataList)
			if(data!=null)
				frenchDataElasticSender.sendDailyDataToElastic(data)
			return data
		}
		return null
	}


	fun extractFrenchDataSinceStartDate(startDate: LocalDate): List<FrenchCovidDailyData>{
		val today=LocalDate.now()
		var date=startDate
		val result=ArrayList<FrenchCovidDailyData>()
		while(date.isBefore(today)){
			val data=this.extractFrenchDataForGivenDay(date)
			if(data!=null){
				println("adding data $data")
				result.add(data)
			}
			date=date.plusDays(1)
		}
		return result
	}

}