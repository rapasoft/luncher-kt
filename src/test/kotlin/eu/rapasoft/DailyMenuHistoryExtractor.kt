package eu.rapasoft

import eu.rapasoft.model.DailyMenu
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.*
import org.junit.Ignore
import org.junit.Test
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

class DailyMenuHistoryExtractor {

    @Ignore
    @Test
    fun extract() {
        runBlocking {
            val dailyMenus = fetchMenus()
                .map { it.await() }
                .flatten()

            val mainDishesDescriptions = dailyMenus
                .map { menu -> menu.mainDishes.map { it.description } }
                .flatten()
            val soupsDescriptions = dailyMenus
                .map { menu -> menu.soups.map { it.description } }
                .flatten()

            launch {
                Files.write(Paths.get("/home/rapasoft/mains.txt"), mainDishesDescriptions)
                Files.write(Paths.get("/home/rapasoft/soups.txt"), soupsDescriptions)
            }
        }
    }

    private fun CoroutineScope.fetchMenus(): List<Deferred<List<DailyMenu>>> {
        return (4..8).map {
            async {
                val date = "2019-02-0$it"

                val urlString = "http://luncherapp.azurewebsites.net/daily-menu/$date"
                val client = HttpClient(Apache) {
                    install(JsonFeature) {
                        serializer = GsonSerializer {
                            serializeNulls()
                            disableHtmlEscaping()
                        }
                    }
                }

                val menu = client.get<List<DailyMenu>> {
                    url(URL(urlString))
                    contentType(ContentType.Application.Json)
                }
                client.close()

                menu
            }
        }
    }

}
