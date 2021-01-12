package volte.commands.parsers

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.TextChannel
import volte.commands.parsers.abs.VolteArgumentParser
import volte.meta.isNumeric
import volte.meta.DiscordUtil

class TextChannelParser : VolteArgumentParser<TextChannel?>() {
    override fun parse(event: CommandEvent, value: String): TextChannel? {
        var tc: TextChannel? = if (value.trim().isNumeric())
            event.guild.getTextChannelById(value) //id check
        else null

        if (tc == null) {
            val channels = event.guild.textChannels.filter {
                it.name.equals(value.replace("  ", "_").replace(" ", "-"), ignoreCase = true) //name check
            }
            if (channels.size == 1) {
                tc = channels.first()
            }

            if (channels.size > 1) return tc
        }

        if (tc == null) {
            val parsed = DiscordUtil.parseChannel(value) // <#id> channel mention check
            if (parsed != null) {
                tc = event.guild.getTextChannelById(value)
            }
        }

        return tc
    }
}