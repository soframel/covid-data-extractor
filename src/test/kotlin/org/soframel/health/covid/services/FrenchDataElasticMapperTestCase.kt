package org.soframel.health.covid.services

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import java.io.InputStream

class FrenchDataElasticMapperTestCase{
    @Test
    fun testDailyMapping(){
        val input: InputStream=this.javaClass.getResourceAsStream("/AllDataByDate-2020-08-20.json")
        var bytes=ByteArray(0)
        IOUtils.readFully(input, bytes)

        val json=String(bytes)
    }
}