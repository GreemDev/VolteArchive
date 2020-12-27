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
        this.aliases = arrayOf("permissionsfor", "perms", "permission")
        this.help = "Shows permissions for a given user, or yourself if you don't provide a user."
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        val member = if (event.args.isEmpty())
            event.member
        else
            Parsers.member().parse(event, event.args)

        if (member == null) {
            event.messageReply {
                setTitle("Invalid user passed as an argument. Try an ID?")
            }
            return
        }

        if (member.id == event.guild.ownerId) {
            event.messageReply {
                setTitle("User is the guild owner, so they have all permissions.")
            }

            return
        }

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            event.messageReply {
                setTitle("User has the Administrator permission, so they have all permissions.")
            }

            return
        }

        val res = DiscordUtil.prettyPermissions(member)

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