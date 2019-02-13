package eu.rapasoft.model

data class FoodAnnotation(val name: String, val regex: Regex) {

    fun apply(input: Food): Food {
        input.description.split(" ").forEach { e ->
            if (regex.matches(e.trim())) {
                return Food(input.description, input.type.plus(FoodType(name)))
            }
        }

        return input
    }

}
