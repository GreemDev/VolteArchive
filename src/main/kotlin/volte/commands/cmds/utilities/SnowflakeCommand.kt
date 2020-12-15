package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.apache.commons.lang3.StringUtils
import volte.meta.createEmbed
import volte.util.DiscordUtil

class SnowflakeCommand : Command() {
    override fun execute(event: CommandEvent) {
        if (StringUtils.isNumeric(event.args).not()) {
            event.message.reply(event.createEmbed("Input must be a snowflake; aka a Discord ID!")).queue()
            return
        }
        //val date = DiscordUtil.parseSnowflake(event.args)
    }
}