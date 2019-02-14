package eu.rapasoft.extractor

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.DailyMenuSource
import eu.rapasoft.service.ConnectionService
import eu.rapasoft.utils.*
import org.nield.kotlinstatistics.toNaiveBayesClassifier
import java.util.*

class Extractor(private val connectionService: ConnectionService) {

    private val soups = loadDictionaryFromClassPath("soups")
    private val mains = loadDictionaryFromClassPath("mains")
    private val other = loadDictionaryFromClassPath("other")

    private val classifier = listOf<FoodClass>().union(soups).union(mains).union(other)
        .toNaiveBayesClassifier(
            featuresSelector = { it.description.splitWords().toSet() },
            categorySelector = { it.category }
        )

    fun extract(dailyMenuSource: DailyMenuSource): DailyMenu {
        val html = connectionService.fetchPageWithReconnects(dailyMenuSource, 3)

        val extractionPath = guessExtractionPath(dailyMenuSource.foodsExtractionUrl)

        val extractedFood = html.selectMainContent(extractionPath)
            .map { Pair(it, classifier.predict(it.splitWords().toSet())) }
            .filter { it.second != null && it.second != "other" }

        return DailyMenu(
            Date(),
            dailyMenuSource,
            toFood("soups", extractedFood),
            toFood("mains", extractedFood)
        )
    }
}