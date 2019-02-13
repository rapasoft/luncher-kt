package eu.rapasoft.extractor

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.DailyMenuSource
import eu.rapasoft.model.Food
import eu.rapasoft.service.ConnectionService
import eu.rapasoft.utils.guessExtractionPath
import eu.rapasoft.utils.selectMainContent
import eu.rapasoft.utils.splitWords
import org.nield.kotlinstatistics.toNaiveBayesClassifier
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class Extractor(private val connectionService: ConnectionService) {

    private fun loadFromClassPath(type: String): List<FoodClass> =
        Files.readAllLines(Paths.get(javaClass.getResource("/dictionary/$type.txt").toURI()))
            .map { FoodClass(it, type) }

    private val soups = loadFromClassPath("soups")
    private val mains = loadFromClassPath("mains")
    private val other = loadFromClassPath("other")

    private class FoodClass(val description: String, val category: String)

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

    private fun toFood(type: String, extractedFood: List<Pair<String, String?>>) =
        extractedFood.filter { it.second == type && it.first.isNotBlank() }.map {
            Food(it.first, emptySet())
        }.toSet()
}