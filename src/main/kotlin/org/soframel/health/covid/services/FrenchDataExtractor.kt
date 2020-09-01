package org.soframel.health.covid.services

import org.eclipse.microprofile.rest.client.inject.RestClient;
import javax.inject.Inject;
import org.soframel.health.covid.client.CoronavirusAPIFrance
import org.soframel.health.covid.elastic.ElasticSender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import org.soframel.health.covid.mappers.FrenchDataElasticMapper
import java.time.LocalDate
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default

@ApplicationScoped
class FrenchDataExtractor {

	@Inject
	@field: RestClient
	lateinit var frenchClient: CoronavirusAPIFrance

	@Inject
	@field: Default
	lateinit var elasticSender: ElasticSender

	@Inject
	@field: Default
	lateinit var mapper: FrenchDataElasticMapper

	
	fun extractFrenchDataForToday(){
		val result=frenchClient.getTodaysData();
		val dataList=result.franceGlobalLiveData;
		println("loaded "+ dataList.size +" entries")
		if(dataList.size>=0){
			this.importDataListIntoElastic(dataList)
		}
	}

	fun importDataListIntoElastic(dataList: List<FrenchCovidDailyData>){
		val list= this.keepOnlyDataToImport(dataList)
		for(d in list){
			val edata=mapper.map(d)
			elasticSender.serializeAndSend(edata)
		}
	}

	fun keepOnlyDataToImport(list: List<FrenchCovidDailyData>): List<FrenchCovidDailyData>{
		return list.filter { d -> this.shouldDataBeImported(d) }
	}

	fun shouldDataBeImported(data: FrenchCovidDailyData): Boolean{
		//do not keep opencovid global data for France, equivalent to ministere-sante data
		//but for regions, some regions are missing -> keep opencovid
		return data.sourceType=="ministere-sante" || (data.sourceType=="opencovid19-fr" && data.code!="FRA")
	}

	/*fun findMinistereSanteData(list: List<FrenchCovidDailyData>): FrenchCovidDailyData?{
		var foundData: FrenchCovidDailyData?=null
		var it=list.iterator()
		while(it.hasNext() && foundData==null){
			val data=it.next()
			if("ministere-sante".equals(data.sourceType)){
				foundData=data
			}
		}
		return foundData
	}*/

	fun extractFrenchDataForGivenDay(day: LocalDate){
		val result=frenchClient.getDataAtDate(day)
		val dataList=result.allFranceDataByDate;
		println("loaded "+ dataList.size +" entries for day "+day)
		if(dataList.size>0){
			this.importDataListIntoElastic(dataList)
		}
	}


	fun extractFrenchDataSinceStartDate(startDate: LocalDate){
		val today=LocalDate.now()
		var date=startDate
		while(date.isBefore(today)){
			this.extractFrenchDataForGivenDay(date)
			date=date.plusDays(1)
		}
	}

}