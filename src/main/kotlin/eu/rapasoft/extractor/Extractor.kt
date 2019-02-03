package eu.rapasoft.extractor

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.DailyMenuSource
import org.koin.standalone.KoinComponent

interface Extractor : KoinComponent {

    fun extract(dailyMenuSource: DailyMenuSource): DailyMenu

}