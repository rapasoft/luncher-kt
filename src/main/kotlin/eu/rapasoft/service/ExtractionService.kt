package eu.rapasoft.service

import eu.rapasoft.extractor.ExtractorFactory
import eu.rapasoft.model.DailyMenu
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ExtractionService(
    private val extractorFactory: ExtractorFactory,
    private val dailyMenuSourceService: DailyMenuSourceService
) {
    val extracted = mutableListOf<DailyMenu>()

    fun extractAll() {
        GlobalScope.launch {
            coroutineScope {
                dailyMenuSourceService.sources.forEach {
                    launch {
                        extracted.add(extractorFactory.selectExtractor(it).extract(it))
                    }
                }
            }
        }
    }
}