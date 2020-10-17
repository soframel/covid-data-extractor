package org.soframel.health.covid.services

import org.eclipse.microprofile.rest.client.inject.RestClient;
import javax.inject.Inject;
import org.soframel.health.covid.client.CoronavirusAPIFrance
import org.soframel.health.covid.elastic.ElasticSender
import org.soframel.health.covid.model.french.FrenchCovidDailyData
import org.soframel.health.covid.mappers.FrenchDataElasticMapper
import java.time.LocalDate
import java.util.*
import java.util.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default

@ApplicationScoped
class FrenchDataExtractor {

	val logger = Logger.getLogger(FrenchDataExtractor::class.qualifiedName)

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
		logger.info("extracting french data for today")
		val result=frenchClient.getAllTodaysData();
		val dataList=result.allLiveFranceData;
		logger.info("loaded "+ dataList.size +" entries")


		val shortList=this.removeDoubles((dataList))
		logger.info("kept "+shortList.size+" entries, from sources")
		//shortList.forEach{print(it.sourceType)}
		elasticSender.serializeAndSendBulk(mapper.map(shortList))
	}

	fun extractFrenchDataForGivenDay(day: LocalDate){
		val result=frenchClient.getDataAtDate(day)
		val dataList=result.allFranceDataByDate;
		logger.info("loaded "+ dataList.size +" entries for day "+day)
		if(dataList.size>0){
			val shortList=this.removeDoubles((dataList))
			logger.info("kept "+shortList.size+" entries")
			//shortList.forEach{print(it.sourceType)}
			elasticSender.serializeAndSendBulk(mapper.map(shortList))
		}
	}


	fun extractFrenchDataSinceStartDate(startDate: LocalDate){
		logger.info("extracting french data since start "+startDate)
		val today=LocalDate.now()
		var date=startDate
		while(date.isBefore(today)){
			this.extractFrenchDataForGivenDay(date)
			date=date.plusDays(1)
		}
	}


	fun deleteAndExtractAllDataSince(date: LocalDate){
		logger.info("deleting and re-extracting data for last week")
		elasticSender.deleteFromElastic("FR", date)
		this.extractFrenchDataSinceStartDate(date)
	}

	/**keep most official data when there is a choice **/
	fun removeDoubles(list: List<FrenchCovidDailyData>): List<FrenchCovidDailyData>{
		val map=HashMap<String,MutableList<FrenchCovidDailyData>>()
		for(data in list){
			if(!data.code.equals("Monde")) { //do not keep world data, it is too incomplete in this dataset
				var regionList = map.get(data.code)
				if (regionList != null) {
					regionList.add(data)
				} else {
					map.set(data.code, mutableListOf(data))
				}
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



}