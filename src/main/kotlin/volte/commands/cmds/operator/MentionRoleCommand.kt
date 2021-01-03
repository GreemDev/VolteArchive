package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Message
import volte.commands.parsers.Parsers
import volte.meta.Constants
import volte.meta.replyInline
import volte.meta.then
import java.util.*

class MentionRoleCommand : Command() {

    init {
        this.name = "mentionrole"
        this.aliases = arrayOf("menro")
        this.help = "Mentions a role."
        this.guildOnly = true
        this.category = Constants.operatorCategory()
    }

    override fun execute(event: CommandEvent) {
        val parsed = Parsers.role().parse(event, event.args)
        if (parsed == null) {
            event replyInline {
                setTitle("Please provide a valid role to mention!")
            }
        } else {
            if (parsed.isMentionable) {
                event.reply(parsed.asMention)
            } else {
                parsed.manager.setMentionable(true) then {
                    event.channel.sendMessage(parsed.asMention).allowedMentions(EnumSet.of(Message.MentionType.ROLE)) then {
                        parsed.manager.setMentionable(false)
                    }
                }
            }

        }
    }
}