package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.apache.commons.lang3.StringUtils
import volte.meta.createEmbed
import volte.meta.messageReply
import volte.meta.prettyPrint
import volte.util.DiscordUtil

class SnowflakeCommand : Command() {

    init {
        this.name = "snowflake"
        this.help = "Shows a snowflake (Discord ID)'s creation date."
        this.guildOnly = true
    }

    override fun execute(event: CommandEvent) {
        if (StringUtils.isNumeric(event.args).not()) {
            event.message.reply(event.createEmbed("Input must be a snowflake; aka a Discord ID!")).queue()
            return
        }
        val inst = DiscordUtil.parseSnowflake(event.args).prettyPrint()
        event.messageReply {
            addField("Date", inst.split(",").first(), true)
            addField("Time", inst.split(",")[1], true)
        }
    }
}