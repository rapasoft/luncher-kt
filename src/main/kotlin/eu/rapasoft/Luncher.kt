package eu.rapasoft

import eu.rapasoft.extractor.ExtractorFactory
import eu.rapasoft.extractor.RepetitiveDayPathExtractor
import eu.rapasoft.extractor.SimplePathExtractor
import eu.rapasoft.service.ConnectionService
import eu.rapasoft.service.DailyMenuSourceService
import eu.rapasoft.service.ExtractionService
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext.startKoin
import org.slf4j.LoggerFactory

fun main() {
    startKoin(listOf(module {
        single { LoggerFactory.getLogger("Luncher") }
        single { DailyMenuSourceService() }
        single { SimplePathExtractor(get()) }
        single { RepetitiveDayPathExtractor(get()) }
        single { ExtractorFactory(get(), get()) }
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
        extractionService.extractAll()

        routing {
            get("/ping") {
                call.respondText { "Pong" }
            }
            get("/menu") {
                call.respond(extractionService.extracted)
            }
        }
    }.start(false)
}

