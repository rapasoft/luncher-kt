package eu.rapasoft.filter

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.Food

class NameSanitizerFilter : DailyMenuFilter {
    override fun filter(dailyMenu: DailyMenu): DailyMenu {
        return dailyMenu.copy(
            soups = dailyMenu.soups.map { s -> removeBS(s) }.toSet(),
            mainDishes = dailyMenu.mainDishes.map { s -> removeBS(s) }.toSet()
        )
    }

    fun removeBS(input: Food): Food {
        var description = input.description
            .replace("""€""".toRegex(), "") // remove eurosymbol
            .replace("""[0-9]+\.""".toRegex(), "") // remove numbering
            .replace("""^[A-Z]:""".toRegex(), "") // remove A:, B:, C:... from line for Lanogi Gurman menu (main dishes)
            .replace(""" [A-Z]:$""".toRegex(), "") // remove A:, B:, C: allergens at the end of line
            .replace("""^(Po. |Ut. |St. |Št. |Pia. )""".toRegex(), "") // remove days from Buddies soups line
            .replace("""/{0,1}[0-9]+\ *g/{0,1}\ *""".toRegex(), "") // remove grams
            .replace("""/{0,1}[0-9]+\ *(ml)/{0,1}\ *""".toRegex(), "") // remove mililters
            .replace("""Menu\ [0-9]+\ *:\ *""".toRegex(), "") // remove leading "Menu : " from the Astra restaurant menu
            .replace("""0[,|\.][0-9]+\s{0,1}l{1}\ """.toRegex(), "") // remove litres
            .replace(""" *(/|\(){0,1}([0-9](,| |/|\)|-)*)+$""".toRegex(), "") // remove trailing alergens
            .replace("""^(Pondelok|Utorok|Streda|Štvrtok|Piatok)\:{0,1}""".toRegex(), "")
            .replace("""^(PONDELOK - |UTOROK - |STREDA - |ŠTVRTOK - |PIATOK - )\:{0,1}""".toRegex(), "")
            .replace("""^([P|p]olievk[a|y])(\s)*:*""".toRegex(), "") // remove starting line with polievka
            .replace("""^(POLIEVKA)(\s)*:""".toRegex(), "") // remove starting line with polievka
            .replace(
                """\(([A-Z],)*[A-Z]\)""".toRegex(),
                ""
            ) // remove (B,V,P), (B,V), (P) etc  from Veg Life (food type like P is pikantne)
            .replace("""^\ *[0-9]+\ *""".toRegex(), "") // remove leading numbers
            .trim()
        if (description.startsWith(") ") || description.startsWith(": ")) {
            description = description.substring(2)
        }
        if (description.endsWith(" Eur")) {
            description = description.substring(0, description.length - 4)
        }

        // Lowercase with starting uppercase
        if (description.length > 2) {
            val lowercasedDescription = StringBuilder()
            lowercasedDescription.append(description[0].toUpperCase())
            lowercasedDescription.append(description.substring(1).toLowerCase())
            description = lowercasedDescription.toString()
        }

        return Food(description, input.type)
    }
}
