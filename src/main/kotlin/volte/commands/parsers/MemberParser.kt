package volte.commands.parsers

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Member
import volte.commands.parsers.abs.VolteArgumentParser
import volte.meta.isNumeric
import volte.meta.DiscordUtil

class MemberParser : VolteArgumentParser<Member?>() {
    override fun parse(event: CommandEvent, value: String): Member? {
        var memberId: String? = if (value.trim().isNumeric())
            value.trim() //id check
        else null

        if (memberId == null) {
            memberId = event.guild.members.firstOrNull {
                it.effectiveName.equals(value, true)
            }?.id
        }

        if (memberId == null) {
            val parsed = DiscordUtil.parseUser(value) //<@id> user mention check
            if (parsed != null) {
                memberId = parsed
            }
        }

        return if (memberId != null) {
            event.guild.retrieveMemberById(memberId).complete()
        } else null
    }
}