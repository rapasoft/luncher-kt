package eu.rapasoft.model

data class DailyMenuSlackMessage(val body: SlackMessageBody) {
    constructor(body: MutableList<DailyMenu>) : this(SlackMessageBody(body.map {
        SlackMessageAttachment(
            it.restaurant.restaurantName,
            it.soups,
            it.mainDishes
        )
    }))

    val status = 200
}

data class SlackMessageBody(val attachments: List<SlackMessageAttachment>) {
    val text = "AI-powered extraction of Denne Menucka!"
    val response_type = "ephemeral"
}

class SlackMessageAttachment {
    val title: String
    val fields: List<Field>

    constructor(title: String, soups: Collection<Food>, mains: Collection<Food>) {
        this.title = title
        this.fields = listOf(
            Field(
                "Soups",
                soups.joinToString("\\n") { it.type.joinToString(" ") { type -> ":${type.category}: " } + it.description },
                true
            ),
            Field(
                "Mains",
                mains.joinToString("\\n") { it.type.joinToString(" ") { type -> ":${type.category}: " } + it.description })
        )
    }

}

data class Field(val title: String, val value: String, val short: Boolean = false)