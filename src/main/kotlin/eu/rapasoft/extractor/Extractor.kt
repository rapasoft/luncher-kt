package eu.rapasoft.extractor

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.DailyMenuSource

interface Extractor {

    fun extract(dailyMenuSource: DailyMenuSource): DailyMenu

}