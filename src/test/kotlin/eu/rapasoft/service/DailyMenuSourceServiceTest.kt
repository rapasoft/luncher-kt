package eu.rapasoft.service

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.slf4j.LoggerFactory

class DailyMenuSourceServiceTest {

    @Before
    fun before() {
        StandAloneContext.startKoin(listOf(module {
            single { LoggerFactory.getLogger("Luncher") }
            single { DailyMenuSourceService() }
        }))
    }

    @After
    fun after() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun getSources() {
        assertEquals(16, DailyMenuSourceService().sources.size)
    }
}