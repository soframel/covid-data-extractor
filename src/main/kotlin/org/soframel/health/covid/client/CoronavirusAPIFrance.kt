package org.soframel.health.covid.client

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.soframel.health.covid.model.french.FrenchResult
import java.time.LocalDate
import javax.enterprise.inject.Default

@Path("/")
@RegisterRestClient
@Default
interface CoronavirusAPIFrance {
	
	@GET
    @Path("/FranceLiveGlobalData")
    @Produces("application/json")
	fun getTodaysData(): FrenchResult
	
	@GET
    @Path("/AllDataByDate")
    @Produces("application/json")
	fun getDataAtDate(@QueryParam("date") date: LocalDate): FrenchResult
}