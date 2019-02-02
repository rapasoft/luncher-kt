package eu.rapasoft.model

data class FoodType(val category: String)

data class Food(
    val description: String,
    val type: Set<FoodType>
)
