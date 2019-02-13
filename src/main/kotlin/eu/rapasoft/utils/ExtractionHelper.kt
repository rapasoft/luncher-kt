package eu.rapasoft.utils

import io.ktor.http.Url
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import org.jsoup.select.Elements

fun guessExtractionPath(url: String): String {
    return when (Url(url).host) {
        "www.zomato.sk" -> "#restaurant > div:nth-child(5)"
        "www.zomato.com" -> "#menu-preview > div.tmi-groups > div:nth-child(1)"
        "restauracie.sme.sk" -> "div.dnesne_menu"
        "www.restaurantpresto.sk" -> "body > div:nth-child(2) > div"
        "www.veglife.sk" -> "#optimizer_front_text-10 > div > div > div.text_block_wrap > div > div > div > div > div > div.pcs-content.pcs-reset"
        else -> "body"
    }
}

fun Document.selectMainContent(extractionPath: String): List<String> {
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

fun String.splitWords() = split(Regex("\\s")).asSequence()
    .map { it.replace(Regex("[^A-Za-z]"), "").toLowerCase() }
    .filter { it.isNotEmpty() }

fun Elements.getLeavesAsText(): List<String> {
    return this.select("*").map { it.ownText() }
}
