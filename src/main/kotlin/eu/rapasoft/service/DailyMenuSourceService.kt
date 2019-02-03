package eu.rapasoft.service

import com.beust.klaxon.Klaxon
import eu.rapasoft.model.DailyMenuSource
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger

class DailyMenuSourceService : KoinComponent {
    private val logger: Logger by inject()
    val sources: List<DailyMenuSource> by lazy {
        val list: List<DailyMenuSource> = Klaxon()
            .parseArray(DailyMenuSourceService::class.java.getResourceAsStream("/sources.json"))!!
        logger.info("Source list: $list")
        list
    }
}