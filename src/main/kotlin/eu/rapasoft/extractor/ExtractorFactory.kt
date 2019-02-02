package eu.rapasoft.extractor

import eu.rapasoft.model.DailyMenuSource

class ExtractorFactory(
    private val simplePathExtractor: SimplePathExtractor,
    private val repetitiveDayPathExtractor: RepetitiveDayPathExtractor
) {
    fun selectExtractor(dailyMenuSource: DailyMenuSource): Extractor {
        if (dailyMenuSource.mainDishesPath.contains("@day")) {
            return repetitiveDayPathExtractor
        }
        return simplePathExtractor
    }
}