package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.meta.Constants
import volte.meta.messageReply
import volte.meta.prettyPrint
import java.time.Instant

class NowCommand : Command() {

    init {
        this.name = "now"
        this.help = "Shows the current time."
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        event.messageReply {
            val now = Instant.now()
                .prettyPrint()
                .split(",")
                .map(CharSequence::trim)

            setDescription(StringBuilder()
                .appendLine("**Date**: ${now.first()}")
                .appendLine("**Time**: ${now[1]}"))
        }
    }
}