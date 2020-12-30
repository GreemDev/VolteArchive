package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.apache.commons.lang3.StringUtils
import volte.meta.Constants
import volte.meta.createEmbed
import volte.meta.messageReply
import volte.meta.prettyPrint
import volte.util.DiscordUtil

class SnowflakeCommand : Command() {

    init {
        this.name = "snowflake"
        this.help = "Shows a snowflake (Discord ID)'s creation date."
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        if (!StringUtils.isNumeric(event.args)) {
            event.messageReply {
                setTitle("Input must be a snowflake; aka a Discord ID!")
            }
            return
        }
        val prettyPrintArr = DiscordUtil.parseSnowflake(event.args).prettyPrint().split(',')
        event.messageReply {
            addField("Date", prettyPrintArr.first(), true)
            addField("Time", prettyPrintArr[1], true)
        }
    }
}