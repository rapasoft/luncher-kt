package eu.rapasoft

import eu.rapasoft.extractor.Extractor
import eu.rapasoft.model.DailyMenuSlackMessage
import eu.rapasoft.service.ConnectionService
import eu.rapasoft.service.DailyMenuSourceService
import eu.rapasoft.service.ExtractionService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext
import org.slf4j.LoggerFactory
import javax.management.timer.Timer
import kotlin.concurrent.scheduleAtFixedRate

fun Application.main() {
    StandAloneContext.startKoin(listOf(module {
        single { LoggerFactory.getLogger("Luncher") }
        single { DailyMenuSourceService() }
        single { Extractor(get()) }
        single { ConnectionService() }
        single { ExtractionService(get(), get()) }
    }))

    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()

            disableHtmlEscaping()
            disableInnerClassSerialization()
            enableComplexMapKeySerialization()
        }
    }

    val extractionService: ExtractionService by inject()

    java.util.Timer().scheduleAtFixedRate(0L, Timer.ONE_HOUR) {
        CoroutineScope(Dispatchers.Default).launch {
            extractionService.extractAll()
        }
    }

    routing {
        static {
            resources("frontend")
            defaultResource("frontend/index.html")
        }
        get("/menu") {
            call.respond(extractionService.extracted)
        }
        get("/menu-slack") {
            call.respond(DailyMenuSlackMessage(extractionService.extracted))
        }
    }
}

