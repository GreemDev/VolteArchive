package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.apache.commons.lang3.StringUtils
import volte.meta.Constants
import volte.meta.replyInline
import volte.meta.prettyPrint
import volte.util.DiscordUtil

class SnowflakeCommand : Command() {

    init {
        this.name = "snowflake"
        this.help = "Shows the date that a snowflake (Discord ID) represents."
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        if (!StringUtils.isNumeric(event.args)) {
            event.replyInline {
                setTitle("Input must be a snowflake; aka a Discord ID!")
            }
            return
        }
        val prettyPrintArr = DiscordUtil.parseSnowflake(event.args).prettyPrint().split(',')
        event.replyInline {
            addField("Date", prettyPrintArr.first(), true)
            addField("Time", prettyPrintArr[1], true)
        }
    }
}