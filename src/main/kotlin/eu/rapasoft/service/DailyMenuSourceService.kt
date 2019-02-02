package eu.rapasoft.service

import com.beust.klaxon.Klaxon
import eu.rapasoft.model.DailyMenuSource

class DailyMenuSourceService {
    val sources: List<DailyMenuSource> by lazy {
        val list: List<DailyMenuSource> = Klaxon()
            .parseArray(DailyMenuSourceService::class.java.getResourceAsStream("/sources.json"))!!
        println(list)
        list
    }
}