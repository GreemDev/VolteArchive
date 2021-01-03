package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.commands.parsers.Parsers
import volte.meta.categories.utility
import volte.meta.getHighestRoleWithColor
import volte.meta.optional
import volte.meta.replyInline
import java.awt.Color

class AvatarCommand : Command() {

    init {
        this.name = "avatar"
        this.help = "Shows a massive avatar for a user, or yourself if none is provided."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        Parsers.member().parse(event, event.args).optional() hasValue {
            event.replyInline {
                setColor(it.getHighestRoleWithColor().valueOrNull()?.color ?: Color.GREEN)
                setImage(it.user.effectiveAvatarUrl)
            }
        } hasNoValue {
            event.replyInline {
                setImage(event.author.effectiveAvatarUrl)
            }
        }
    }
}