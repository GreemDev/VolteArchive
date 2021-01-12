package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.meta.categories.utility
import volte.meta.prettyPrint
import volte.meta.replyInline
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

            setDescription(
                buildString {
                    appendLine("**Date**: ${now.first()}")
                    appendLine("**Time**: ${now[1]}")
                }
            )
        }
    }
}