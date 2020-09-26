package org.soframel.health.covid.elastic

import org.soframel.health.covid.mappers.SwissDataElasticMapper
import org.soframel.health.covid.model.CovidElasticData
import java.util.logging.Logger
import javax.enterprise.context.ApplicationScoped

/**
 * aggregate a list of CovidElasticData into one global, country-wide data
 */
@ApplicationScoped
class ElasticDataAggregator {

    val logger = Logger.getLogger(ElasticDataAggregator::class.qualifiedName)

    val source="AGGREGATE"

    fun aggregate(list: List<CovidElasticData>): CovidElasticData{
        var d=CovidElasticData()

        if(list.size>0) {

            d.country = list[0].country
            d.date=list[0].date
            d.region=""
            d.source=source

            logger.info("aggregating data for "+d.country+" from "+list.size+" data")

            d.totalDeaths = list.map{it.totalDeaths}.sum()
            d.currentlyInReanimation = list.map{e -> e.currentlyInReanimation}.sum()
            d.currentlyHospitalized = list.map{e -> e.currentlyHospitalized}.sum()
            d.newHospitalisations = list.map{e -> e.newHospitalisations}.sum()
            d.totalCases = list.map{e -> e.totalCases}.sum()
            d.totalTested = list.map{e -> e.totalTested}.sum()
            d.newPositiveTests=list.map{e -> e.newPositiveTests}.sum()
            d.newReanimations=list.map{e -> e.newReanimations}.sum()
            d.totalCured=list.map{e -> e.totalCured}.sum()

            d.computeAdditionalValues(list.map{e -> e.population}.sum())
        }
        else{
            logger.warning("No data to aggregate")
        }

        return d
    }
}