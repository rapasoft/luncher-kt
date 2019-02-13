package eu.rapasoft.extractor

import eu.rapasoft.service.ConnectionService
import eu.rapasoft.service.DailyMenuSourceService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.slf4j.LoggerFactory

class ExtractorTest : KoinComponent {

    private val extractor: Extractor by inject()

    @Before
    fun before() {
        StandAloneContext.startKoin(listOf(module {
            single { ConnectionService() }
            single { LoggerFactory.getLogger("Luncher") }
            single { Extractor(get()) }
            single { DailyMenuSourceService() }
        }))
    }

    @After
    fun after() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun extract() {
        DailyMenuSourceService().sources.forEach {
            extractor.extract(it)
        }
    }
}