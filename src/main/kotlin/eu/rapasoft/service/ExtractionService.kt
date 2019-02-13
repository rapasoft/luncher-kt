package eu.rapasoft.service

import eu.rapasoft.extractor.Extractor
import eu.rapasoft.filter.EmptyMenuFilter
import eu.rapasoft.filter.FoodAnnotationFilter
import eu.rapasoft.filter.NameSanitizerFilter
import eu.rapasoft.model.DailyMenu
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ExtractionService(
    private val extractor: Extractor,
    private val dailyMenuSourceService: DailyMenuSourceService
) {
    val extracted = mutableListOf<DailyMenu>()
    private val filters = listOf(
        NameSanitizerFilter(),
        FoodAnnotationFilter(),
        EmptyMenuFilter()
    )

    fun extractAll() {
        GlobalScope.launch {
            coroutineScope {
                dailyMenuSourceService.sources.forEach { dailyMenuSource ->
                    launch {
                        var dailyMenu = extractor.extract(dailyMenuSource)

                        filters.forEach { dailyMenu = it.filter(dailyMenu) }

                        extracted.add(dailyMenu)
                    }
                }
            }
        }
    }
}