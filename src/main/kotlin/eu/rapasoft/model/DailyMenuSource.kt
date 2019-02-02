package eu.rapasoft.model

data class DailyMenuSource(
    val restaurantName: String,
    val foodsExtractionUrl: String,
    val webPage: String,
    val lowestPrice: Double,
    val highestPrice: Double,
    val mainDishesPath: String,
    val soupsPath: String,
    val longitude: Double,
    val latitude: Double
)