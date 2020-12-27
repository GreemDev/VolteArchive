package volte.commands.parsers

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Member
import org.apache.commons.lang3.StringUtils
import volte.commands.parsers.abs.VolteArgumentParser
import volte.util.DiscordUtil

class MemberParser : VolteArgumentParser<Member?>() {
    override fun parse(event: CommandEvent, value: String): Member? {
        var member: Member? = if (StringUtils.isNumeric(value))
            event.guild.getMemberById(value) //id check
        else null

        if (member == null) {
            member = event.guild.members.firstOrNull {
                it.effectiveName.equals(value, true) or //username/nickname check
                        (it.id == value)
            }
        }

        if (member == null) {
            val parsed = DiscordUtil.parseUser(value) //<@id> user mention check
            if (parsed != null) {
                member = event.guild.getMemberById(value)
            }
        }

        return member
    }
}