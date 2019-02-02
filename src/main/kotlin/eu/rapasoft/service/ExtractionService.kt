package eu.rapasoft.service

import eu.rapasoft.extractor.ExtractorFactory
import eu.rapasoft.model.DailyMenu
import kotlinx.coroutines.*

class ExtractionService(
    private val extractorFactory: ExtractorFactory,
    private val dailyMenuSourceService: DailyMenuSourceService
) {
    val extracted = mutableListOf<DailyMenu>()

    fun extractAll() {
        GlobalScope.launch {
            coroutineScope {
                dailyMenuSourceService.sources.forEach {
                    extracted.add(withContext(Dispatchers.Default) {
                        extractorFactory.selectExtractor(it).extract(it)
                    })
                }
            }
        }
    }
}