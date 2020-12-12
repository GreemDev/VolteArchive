package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.apache.commons.lang3.StringUtils

class SnowflakeCommand : Command() {
    override fun execute(event: CommandEvent) {
        if (StringUtils.isNumeric(event.args).not()) {

        }
    }
}