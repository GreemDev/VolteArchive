package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.commands.parsers.Parsers
import volte.meta.Constants
import volte.meta.getHighestRoleWithColor
import volte.meta.replyInline
import volte.meta.optional
import java.awt.Color

class AvatarCommand : Command() {

    init {
        this.name = "avatar"
        this.help = "Shows a massive avatar for a user, or yourself if none is provided."
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        val member = Parsers.member().parse(event, event.args).optional()
        member.ifPresent {
            event.replyInline {
                setColor(it.getHighestRoleWithColor().valueOrNull()?.color ?: Color.GREEN)
                setImage(it.user.effectiveAvatarUrl)
            }
        }
        member.ifNotPresent {
            event.replyInline {
                setImage(event.author.effectiveAvatarUrl)
            }
        }
    }
}