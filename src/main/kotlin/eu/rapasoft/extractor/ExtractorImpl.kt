package eu.rapasoft.extractor

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.DailyMenuSource
import eu.rapasoft.service.ConnectionService
import eu.rapasoft.utils.guessExtractionPath
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import org.jsoup.select.Elements
import org.nield.kotlinstatistics.toNaiveBayesClassifier
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class ExtractorImpl(private val connectionService: ConnectionService) : Extractor {

    private fun loadFromClassPath(type: String): List<Food> =
        Files.readAllLines(Paths.get(javaClass.getResource("/dictionary/$type.txt").toURI()))
            .map { Food(it, type) }

    private val soups = loadFromClassPath("soups")
    private val mains = loadFromClassPath("mains")
    private val other = loadFromClassPath("other")

    private class Food(val description: String, val category: String)

    private val classifier = listOf<Food>().union(soups).union(mains).union(other)
        .toNaiveBayesClassifier(
            featuresSelector = { it.description.splitWords().toSet() },
            categorySelector = { it.category }
        )

    override fun extract(dailyMenuSource: DailyMenuSource): DailyMenu {
        val html = connectionService.fetchPageWithReconnects(dailyMenuSource, 3)

        val extractionPath = guessExtractionPath(dailyMenuSource.foodsExtractionUrl)

        println("=============${dailyMenuSource.restaurantName}=============")
        html.selectMainContent(extractionPath)
            .map { Pair(it, classifier.predict(it.splitWords().toSet())) }
            .filter { it.second != null }
            .filter { it.second == "mains" || it.second == "soups" }
            .forEach { println(it.first) }

        return DailyMenu(Date(), dailyMenuSource, emptySet(), emptySet())
    }
}

private fun Document.selectMainContent(extractionPath: String): List<String> {
    return this
        .select(extractionPath)
        .getLeavesAsText()
        .asSequence()
        .map { Jsoup.clean(it, Whitelist.simpleText()) }
        .map { it.replace("&nbsp;", "") }
        .map { it.trim() }
        .filter { it.length > 5 }
        .toList()
}

private fun String.splitWords() = split(Regex("\\s")).asSequence()
    .map { it.replace(Regex("[^A-Za-z]"), "").toLowerCase() }
    .filter { it.isNotEmpty() }

private fun Elements.getLeavesAsText(): List<String> {
    return this.select("*").map { it.ownText() }
}
