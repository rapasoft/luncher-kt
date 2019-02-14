package eu.rapasoft

import eu.rapasoft.extractor.Extractor
import eu.rapasoft.service.ConnectionService
import eu.rapasoft.service.DailyMenuSourceService
import eu.rapasoft.service.ExtractionService
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
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext.startKoin
import org.slf4j.LoggerFactory
import java.util.*
import javax.management.timer.Timer.ONE_HOUR
import kotlin.concurrent.scheduleAtFixedRate

fun main() {
    startKoin(listOf(module {
        single { LoggerFactory.getLogger("Luncher") }
        single { DailyMenuSourceService() }
        single { Extractor(get()) }
        single { ConnectionService() }
        single { ExtractionService(get(), get()) }
    }))
    embeddedServer(Netty, port = 8080) {
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

        Timer().scheduleAtFixedRate(0L, ONE_HOUR) {
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
        }
    }.start(false)
}

