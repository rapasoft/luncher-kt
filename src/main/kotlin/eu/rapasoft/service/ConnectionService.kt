package eu.rapasoft.service

import eu.rapasoft.model.DailyMenuSource
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class ConnectionService {

    private val emptyDocument = Document.createShell("http://itera.no")

    fun fetchPageWithReconnects(dailyMenuSource: DailyMenuSource, retries: Int): Document {
        val foodsExtractionUrl = dailyMenuSource.foodsExtractionUrl
        if (foodsExtractionUrl.isEmpty()) {
            println("Nothing to download from  ${dailyMenuSource.restaurantName}, because foods extraction URL is empty")
            return emptyDocument
        }

        try {
            return Jsoup.connect(foodsExtractionUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
                .referrer("http://www.google.com")
                .followRedirects(true)
                .validateTLSCertificates(false)
                .timeout(10000)
                .get()

        } catch (e: SocketTimeoutException) {
            println("Failed to download from $foodsExtractionUrl, $retries more retries left")
            return if (retries > 0) {
                fetchPageWithReconnects(dailyMenuSource, retries - 1)
            } else {
                println("Failed to download from $foodsExtractionUrl, because exception occured: ${e.message}")
                emptyDocument
            }
        } catch (e: SSLHandshakeException) {
            println("Failed to download from $foodsExtractionUrl, because exception occured: ${e.message}")
            return emptyDocument
        } catch (e: UnknownHostException) {
            println("Failed to download from $foodsExtractionUrl, because exception occured (UnknownHostException): ${e.message}")
            return emptyDocument
        } catch (e: HttpStatusException) {
            println("Failed to fetch URL for $foodsExtractionUrl, because exception occured: ${e.message}")
            return emptyDocument
        } catch (e: Exception) {
            println("Unexpected exception was thrown for $foodsExtractionUrl: ${e.message}")
            return emptyDocument
        }
    }

}
