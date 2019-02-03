package eu.rapasoft.extractor


import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.DailyMenuSource
import eu.rapasoft.model.Food
import eu.rapasoft.service.ConnectionService
import org.jsoup.nodes.Document
import org.koin.standalone.inject
import org.slf4j.Logger
import java.time.LocalDate
import java.util.*
import javax.script.ScriptEngineManager

class RepetitiveDayPathExtractor(
    private val connectionService: ConnectionService
) : Extractor {

    private val logger: Logger by inject()

    override fun extract(dailyMenuSource: DailyMenuSource): DailyMenu {

        val html = connectionService.fetchPageWithReconnects(dailyMenuSource, 3)

        val extracted = DailyMenu(
            Date(),
            dailyMenuSource,
            retrieveList(dailyMenuSource.soupsPath, html),
            retrieveList(dailyMenuSource.mainDishesPath, html)
        )

        logger.info("Extracted ${extracted.soups}, ${extracted.mains}")

        return extracted
    }

    private fun retrieveList(path: String?, html: Document): Set<Food> {
        if (path != null && path != "") {
            val adjustedPath = injectCurrentDateIntoPath(path)
            return html.select(adjustedPath)
                .map { el -> Food(el.text(), emptySet()) }
                .filter { food -> !food.description.isEmpty() }
                .toSet()

        }
        return emptySet()
    }

    // TODO: The code below is still way too procedural / Java 7. Refactor it!

    private fun injectCurrentDateIntoPathForSelector(path: String, selectorType: String): String {
        val localDate = LocalDate.now()
        var adjustedPath = path.replace("@day", localDate.dayOfWeek.toString())
        var startIndex = adjustedPath.indexOf(selectorType)

        while (startIndex >= 0) {
            val endIndex = getNthChildSelectorEnd(adjustedPath, startIndex + selectorType.length)
            val selectorExpressionResult =
                evaluateExpression(adjustedPath.substring(startIndex + selectorType.length, endIndex))
            adjustedPath =
                adjustedPath.replaceRange(startIndex + selectorType.length, endIndex, selectorExpressionResult)
            startIndex = adjustedPath.indexOf(selectorType, startIndex + 1)
        }

        return adjustedPath
    }

    private fun injectCurrentDateIntoPath(path: String): String {
        var adjPath = injectCurrentDateIntoPathForSelector(path, "nth-child(")
        adjPath = injectCurrentDateIntoPathForSelector(adjPath, "lt(")
        adjPath = injectCurrentDateIntoPathForSelector(adjPath, "gt(")
        return injectCurrentDateIntoPathForSelector(adjPath, "nth-of-type(")
    }

    private fun getNthChildSelectorEnd(path: String, startIndex: Int): Int {
        var index = startIndex
        var parenthesis = 1
        while (index < path.length) {
            val stringAtIndex = path.substring(index, index + 1)

            if (stringAtIndex == "(") {
                parenthesis += 1
            } else if (stringAtIndex == ")") {
                parenthesis -= 1
            }

            if (parenthesis == 0) {
                return index
            } else {
                index += 1
            }
        }
        return 0
    }

    private fun evaluateExpression(expr: String): String {
        val scriptManager = ScriptEngineManager()
        val engine = scriptManager.getEngineByName("JavaScript")
        var result = "0"
        try {
            val evalResult = engine.eval(expr).toString()
            if (evalResult.indexOf(".") > -1) {
                result = evalResult.substring(0, evalResult.indexOf("."))
            } else {
                result = evalResult
            }
        } catch (e: Exception) {
            //TODO ignored?
        }
        return result
    }

}