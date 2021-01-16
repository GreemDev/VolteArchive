package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.lib.meta.categories.utility
import volte.lib.meta.prettyPrint
import volte.lib.meta.replyInline
import java.time.Instant

class NowCommand : Command() {

    init {
        this.name = "now"
        this.help = "Shows the current time."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        event.replyInline {
            val now = Instant.now()
                .prettyPrint()
                .split(",")
                .map(CharSequence::trim)

            buildDescription {
                appendLine("**Date**: ${now.first()}")
                appendLine("**Time**: ${now[1]}")
            }

        }
    }
}