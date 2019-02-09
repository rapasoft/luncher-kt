package eu.rapasoft.model

import java.util.*

data class DailyMenu(
    val date: Date,
    val restaurant: DailyMenuSource,
    val soups: Set<Food>,
    val mainDishes: Set<Food>
)