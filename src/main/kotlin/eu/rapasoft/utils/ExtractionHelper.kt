package eu.rapasoft.utils

import io.ktor.http.Url

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
