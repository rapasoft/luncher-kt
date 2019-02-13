package eu.rapasoft.filter

import eu.rapasoft.model.DailyMenu

class EmptyMenuFilter : DailyMenuFilter {
    override fun filter(dailyMenu: DailyMenu): DailyMenu {
        return dailyMenu.copy(
            soups = dailyMenu.soups.filter { it.description.isNotBlank() }.toSet(),
            mainDishes = dailyMenu.mainDishes.filter { it.description.isNotBlank() }.toSet()
        )
    }
}