package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import volte.meta.categories.utility
import volte.meta.replyInline
import volte.meta.DiscordUtil

class PermissionsCommand : Command() {

    init {
        this.name = "permissions"
        this.aliases = arrayOf("perms", "permission")
        this.help = "Shows permissions for yourself."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        if (event.member.isOwner) {
            event.replyInline {
                setTitle("User is the guild owner, so they have all permissions.")
            }

            return
        }

        if (event.member.hasPermission(Permission.ADMINISTRATOR)) {
            event.replyInline {
                setTitle("User has the Administrator permission, so they have all permissions.")
            }
        } else {
            val (allowed, denied) = DiscordUtil.prettyPermissions(event.member)

            val allowedStr = allowed.joinToString("\n", transform = ::formatPermissionName)
            val deniedStr = denied.joinToString("\n", transform = ::formatPermissionName)

            event.replyInline {
                addField("Allowed", if (allowed.isEmpty()) "- None" else allowedStr, true)
                addField("Denied", if (denied.isEmpty()) "- None" else deniedStr, true)
            }
        }


    }

    private fun formatPermissionName(perm: Permission): String {
        val name = perm.name.toLowerCase()
        val parts = name.split("_")
        if (parts.isEmpty()) return name.capitalize()
        return "- " + parts.joinToString(" ", transform = {
            if (it.length == 3 && !it.equals("ban", true)) it.toUpperCase()
            else it.capitalize()
        })

    }
}