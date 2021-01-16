package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import volte.commands.parsers.Parsers
import volte.lib.meta.categories.operator
import volte.lib.meta.replyInline
import volte.lib.meta.then
import java.util.*

class MentionRoleCommand : Command() {

    init {
        this.name = "mentionrole"
        this.aliases = arrayOf("menro")
        this.help = "Mentions a role."
        this.guildOnly = true
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        val parsed = Parsers.parse<Role>(event, event.args)
        if (parsed == null) {
            event replyInline {
                title("Please provide a valid role to mention!")
            }
        } else {
            if (parsed.isMentionable) {
                event.reply(parsed.asMention)
            } else {
                parsed.manager.setMentionable(true) then {
                    event.channel.sendMessage(parsed.asMention)
                        .allowedMentions(EnumSet.of(Message.MentionType.ROLE)) then {
                        parsed.manager.setMentionable(false)
                    }
                }
            }

        }
    }
}