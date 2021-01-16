package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Member
import volte.commands.parsers.Parsers
import volte.lib.kjda.embed
import volte.lib.meta.categories.utility
import volte.lib.meta.getHighestRoleWithColor
import volte.lib.meta.optional
import java.awt.Color

class AvatarCommand : Command() {

    init {
        this.name = "avatar"
        this.help = "Shows a massive avatar for a user, or yourself if none is provided."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        Parsers.parse<Member>(event, event.args).optional() hasValue {
            event.message.reply(embed {
                color(it.getHighestRoleWithColor().valueOrNull()?.color ?: Color.GREEN)
                image(it.user.effectiveAvatarUrl)
            })
        } hasNoValue {
            event.message.reply(embed {
                image(event.author.effectiveAvatarUrl)
            })
        }
    }
}