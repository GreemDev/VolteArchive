package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import volte.commands.parsers.Parsers
import volte.meta.Constants
import volte.meta.messageReply
import volte.util.DiscordUtil

class PermissionsCommand : Command() {

    init {
        this.name = "permissions"
        this.aliases = arrayOf("perms", "permission")
        this.help = "Shows permissions for yourself."
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        if (event.member.isOwner) {
            event.messageReply {
                setTitle("User is the guild owner, so they have all permissions.")
            }

            return
        }

        if (event.member.hasPermission(Permission.ADMINISTRATOR)) {
            event.messageReply {
                setTitle("User has the Administrator permission, so they have all permissions.")
            }

            return
        }

        val res = DiscordUtil.prettyPermissions(event.member)

        val allowedStr = res.allowed.joinToString("\n", transform = this::formatPermissionName)
        val deniedStr = res.denied.joinToString("\n", transform = this::formatPermissionName)

        event.messageReply {
            addField("Allowed", if (allowedStr.isEmpty()) "- None" else allowedStr, true)
            addField("Denied", if (deniedStr.isEmpty()) "- None" else deniedStr, true)
        }
    }

    private fun formatPermissionName(perm: Permission): String {
        val name = perm.name.toLowerCase()
        val parts = name.split("_")
        if (parts.isEmpty()) return name.capitalize()
        return "- " + parts.joinToString(" ", transform = String::capitalize)

    }
}