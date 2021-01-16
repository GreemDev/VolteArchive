package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.lib.meta.categories.utility
import volte.lib.meta.*

class SnowflakeCommand : Command() {

    init {
        this.name = "snowflake"
        this.help = "Shows the date that a snowflake (Discord ID) represents."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        if (!event.args.isNumeric()) {
            event.replyInline {
                title("Input must be a snowflake; aka a Discord ID!")
            }
            return
        }
        val prettyPrint = DiscordUtil.parseSnowflake(event.args).prettyPrint().split(',')
        event.replyInline {
            fields {
                inline("Date", prettyPrint[0])
                inline("Time", prettyPrint[1])
            }
        }
    }
}