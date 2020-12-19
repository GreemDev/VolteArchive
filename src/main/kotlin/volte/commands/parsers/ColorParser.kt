package volte.commands.parsers

import com.jagrosh.jdautilities.command.CommandEvent
import volte.commands.parsers.abstractions.VolteArgumentParser
import java.awt.Color

class ColorParser : VolteArgumentParser<Color?>() {
    override fun parse(event: CommandEvent, value: String): Color? {
        var color: Color? = null

        try {
            val split = value.split(";")
            val r = split[0].toIntOrNull()
            val g = split[1].toIntOrNull()
            val b = split[2].toIntOrNull()

            if ((r == null || g == null || b == null) || (r > 255 || g > 255 || b > 255)) return color

            color = Color(r, g, b)
        } catch (e: Exception) {

        }

        return color

    }
}