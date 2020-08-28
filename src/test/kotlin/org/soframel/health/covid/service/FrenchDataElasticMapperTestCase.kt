package org.soframel.health.covid.service

import org.junit.jupiter.api.Test
import java.io.InputStream

class FrenchDataElasticMapperTestCase{
    @Test
    fun testDailyMapping(){
        val input: InputStream=this.javaClass.getResourceAsStream("/AllDataByDate-2020-08-20.json")
    }
}