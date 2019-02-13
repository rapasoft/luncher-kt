package eu.rapasoft.filter

import eu.rapasoft.model.DailyMenu
import eu.rapasoft.model.Food
import eu.rapasoft.model.FoodAnnotation

class FoodAnnotationFilter : DailyMenuFilter {

    private val annotationList = listOf(
        FoodAnnotation("chicken", """(kura.*|morka|mor[c|č|ć]a.*|husa.*|ka[c|č|ć]ica|ka[c|č|ć]acie|ka[c|č|ć]ka|perli[c|č|ć]k.*|sliepk.*|slepa[c|č|ć]ie|holub.*)""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("cow", """(hov[ä|a]dz.*|ro[š|s]tenka|svie[c|č|ć]ko.*)""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("pig", """(brav[c|č|ć].*|krkovi[c|č|ć]k.*|panenk.*|b[o|ô][c|č|ć]ik.*|pliec.*|kolienk.*)""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("hamburger", """(hamburg.*)|(burger)""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("pizza", """(pizza.*)""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("pasta","""(cestovin.*|penne|[š|]pagh{0,1}ett{0,1}[i|y]|tagliatelle|bulgur|gnocchi|la[s|z]ag{0,1}ne|fusilli|tortellini|farfalle|pirohy|pappardelle|makar[ó|o]ny|maltagliati|rigatoni|fettucine|lasagne|linguine|cavatappi|ravioli|canelloni|cappelleti|fagotini|girandole|maccheroni|sedanini|sedani|rezance|sl[i|í][ž|z]e)""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("salad", """([š|s]al[a|á]t|salad).*""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("fish", """(ryba.*|pstruh|losos|zub[a|á][c|č]|[š|s][t|ť]uk.*|kapor|kapra|karas|pangasi.*|treska|makrela|[z|ž]ralok|sumec|sum[c|č|ć]ek|tuniak|mahi)""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("cake", """(buchta|buchty|pala[c|č|ć]inka|pala[c|č|ć]inky|lievance|[š|s][ú|u][l|ľ]ance|[š|s]i[š|s]ky|buchti[c|č|ć]ky|n[a|á]kyp|gule)""".toRegex(RegexOption.IGNORE_CASE)),
        FoodAnnotation("deeg", """(divin.*|jele[n|ň].*|srn.*|divia.*)""".toRegex(RegexOption.IGNORE_CASE))
    )

    override fun filter(dailyMenu: DailyMenu): DailyMenu {
        return dailyMenu.copy(
            soups = dailyMenu.soups.map { s -> addAnnotations(s) }.toSet(),
            mainDishes = dailyMenu.mainDishes.map { s -> addAnnotations(s) }.toSet()
        )
    }

    private fun addAnnotations(food: Food): Food {
        var mutableInput = food
        annotationList.forEach { annotation -> mutableInput = annotation.apply(mutableInput) }
        return mutableInput
    }

}
