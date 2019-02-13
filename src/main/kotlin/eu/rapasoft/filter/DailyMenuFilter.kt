package eu.rapasoft.filter

import eu.rapasoft.model.DailyMenu

interface DailyMenuFilter {
    fun filter(dailyMenu: DailyMenu): DailyMenu
}