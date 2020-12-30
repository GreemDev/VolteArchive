package volte.commands.parsers

import com.jagrosh.jdautilities.command.CommandEvent
import volte.commands.parsers.abs.VolteArgumentParser

class BooleanParser : VolteArgumentParser<Boolean?>() {

    private val trueValues = arrayOf(
        "true", "y", "yes", "ye", "yep", "yeah", "sure", "si", "affirmative", "yar", "aff", "ya", "da", "yas",
        "enable", "yip", "positive", "1"
    )

    private val falseValues = arrayOf(
        "false", "n", "no", "nah", "na", "nej", "nope", "nop", "neg", "negatory", "disable", "nay", "negative",
        "0"
    )

    override fun parse(event: CommandEvent, value: String): Boolean? = when {
        trueValues.contains(value) -> true
        falseValues.contains(value) -> false
        else -> null
    }
}