package eu.rapasoft.extractor

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.DailyMenuSource
import eu.rapasoft.service.ConnectionService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import org.nield.kotlinstatistics.toNaiveBayesClassifier
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


class TotalBrutalMachineLearningExtractor(private val connectionService: ConnectionService) : Extractor {

    private val soups = Files.readAllLines(Paths.get(javaClass.getResource("/dictionary/soups.txt").toURI()))
        .map { Food(it, "soup") }
    private val mains = Files.readAllLines(Paths.get(javaClass.getResource("/dictionary/mains.txt").toURI()))
        .map { Food(it, "mains") }
    private val other = Files.readAllLines(Paths.get(javaClass.getResource("/dictionary/other.txt").toURI()))
        .map { Food(it, "other") }

    private class Food(val description: String, val category: String)

    private val classifier = listOf<Food>().union(soups).union(mains).union(other)
        .toNaiveBayesClassifier(
            featuresSelector = { it.description.splitWords().toSet() },
            categorySelector = { it.category }
        )

    override fun extract(dailyMenuSource: DailyMenuSource): DailyMenu {
        val html = connectionService.fetchPageWithReconnects(dailyMenuSource, 3)

        println(dailyMenuSource.restaurantName)
        html.splitToLines()
            .map { Pair(it, classifier.predict(it.splitWords().toSet())) }
            .filter { it.second != null }
            .filter { it.second == "mains" }
            .forEach { println(it.first) }

        return DailyMenu(Date(), dailyMenuSource, emptySet(), emptySet())
    }

    private fun Document.splitToLines(): List<String> {
        return this.getLeavesAsText()
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
}

private fun Document.getLeavesAsText(): List<String> {
    return this.body().select("*").map { it.ownText() }
}
