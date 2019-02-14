package eu.rapasoft.service

import eu.rapasoft.extractor.Extractor
import eu.rapasoft.filter.EmptyMenuFilter
import eu.rapasoft.filter.FoodAnnotationFilter
import eu.rapasoft.filter.NameSanitizerFilter
import eu.rapasoft.model.DailyMenu
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger

class ExtractionService(
    private val extractor: Extractor,
    private val dailyMenuSourceService: DailyMenuSourceService
) : KoinComponent {
    val extracted = mutableListOf<DailyMenu>()
    private val logger: Logger by inject()
    private val filters = listOf(
        NameSanitizerFilter(),
        FoodAnnotationFilter(),
        EmptyMenuFilter()
    )

    suspend fun extractAll() {
        coroutineScope {
            val dailyMenuSources = dailyMenuSourceService.sources

            dailyMenuSources.forEach { dailyMenuSource ->
                launch {
                    var dailyMenu = extractor.extract(dailyMenuSource)

                    filters.forEach { dailyMenu = it.filter(dailyMenu) }

                    extracted.add(dailyMenu)

                    logger.info(
                        "${dailyMenuSource.restaurantName} - ${dailyMenu.soups.size} soups, ${dailyMenu.mainDishes.size} mains"
                    )
                }
            }
        }

    }
}