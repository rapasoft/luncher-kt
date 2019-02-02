package eu.rapasoft.extractor

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.DailyMenuSource
import eu.rapasoft.model.Food
import eu.rapasoft.service.ConnectionService
import org.jsoup.nodes.Document
import java.util.*

class SimplePathExtractor(private val connectionService: ConnectionService) : Extractor {

    private fun extractFoodList(path: String, html: Document): Set<Food> {
        return if (path.isEmpty()) emptySet() else html.select(path)
            .map { el -> Food(el.text(), emptySet()) }
            .filter { food -> !food.description.isEmpty() }
            .toSet()
    }


    override fun extract(dailyMenuSource: DailyMenuSource): DailyMenu {
        val html = connectionService.fetchPageWithReconnects(dailyMenuSource, 3)

        val extracted = DailyMenu(
            Date(),
            dailyMenuSource,
            extractFoodList(dailyMenuSource.soupsPath, html),
            extractFoodList(dailyMenuSource.mainDishesPath, html)
        )

        println("Extracted $extracted")

        return extracted
    }
}