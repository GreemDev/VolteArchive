package volte.commands.parsers

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Role
import org.apache.commons.lang3.StringUtils
import volte.commands.parsers.abstractions.VolteArgumentParser
import volte.util.DiscordUtil

class RoleParser : VolteArgumentParser<Role?>() {

    override fun parse(event: CommandEvent, value: String): Role? {
        var role: Role? = if (StringUtils.isNumeric(value))
            event.guild.getRoleById(value) //id check
        else null

        if (role == null) {
            val roles = event.guild.roles.filter {
                it.name.equals(value, true) //name check
            }
            if (roles.size == 1) {
                role = roles.first()
            }
        }

        if (role == null) {
            val parsed = DiscordUtil.parseRole(value) // <@&id> role mention check
            if (parsed != null) {
                role = event.guild.getRoleById(value)
            }
        }

        return role
    }
}