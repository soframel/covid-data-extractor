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
		/*if(dataList.size>=0){
			this.importDataListIntoElastic(dataList)
		}*/
		val shortList=this.removeDoubles((dataList))
		print("kept "+shortList.size+" entries, from sources: ")
		shortList.forEach{print(it.sourceType)}
		this.importDataListIntoElastic(shortList)
	}

	/**keep most official data when there is a choice **/
	fun removeDoubles(list: List<FrenchCovidDailyData>): List<FrenchCovidDailyData>{
		val map=HashMap<String,MutableList<FrenchCovidDailyData>>()
		for(data in list){
			var regionList=map.get(data.code)
			if(regionList!=null){
				regionList.add(data)
			}
			else{
				map.set(data.code, mutableListOf(data))
			}
		}

		val list= mutableListOf<FrenchCovidDailyData>()
		for(key in map.keys){
			val regionList=map.get(key)
			if(regionList!=null) {
				//keep official data
				val official = regionList.filter { it.sourceType.equals("ministere-sante") || it.sourceType.contains("sante-publique") }
				if(official.isNotEmpty()){
					list.add(official[0])
				}
				else{
					list.add(regionList[0])
				}
			}
		}
		return list
	}

	fun importDataListIntoElastic(dataList: List<FrenchCovidDailyData>){
		val list= this.keepOnlyDataToImport(dataList)
		elasticSender.serializeAndSendBulk(mapper.map(list))
	}

	fun keepOnlyDataToImport(list: List<FrenchCovidDailyData>): List<FrenchCovidDailyData>{
		return list.filter { d -> this.shouldDataBeImported(d) }
	}

	fun shouldDataBeImported(data: FrenchCovidDailyData): Boolean{
		//do not keep opencovid global data for France, equivalent to ministere-sante data
		//but for regions, some regions are missing -> keep opencovid
		return data.sourceType.equals("ministere-sante") || (data.sourceType.equals("sante-publique-france-data"))
		// || data.sourceType.equals("opencovid19-fr") && !data.code.equals("FRA")
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