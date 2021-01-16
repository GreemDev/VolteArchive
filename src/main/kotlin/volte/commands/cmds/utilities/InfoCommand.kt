package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.JDAInfo
import volte.lib.meta.*
import volte.lib.meta.categories.utility

class InfoCommand : Command() {

    init {
        this.name = "info"
        this.aliases = arrayOf("botinfo")
        this.help = "Shows various information about Volte."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        event.replyInline {
            fields {
                inline("Version", Version.formatted())
                inline("Author", "Greem#1337, and contributors on GitHub")
                normal("Language/Runtime/Library",
                    buildString {
                        appendLine("Kotlin ${KotlinVersion.CURRENT}")
                        appendLine("Java ${try {
                            System.getProperty("java.vm.version").split('+').first()
                        } catch (e: Exception) { System.getProperty("java.version") }}")
                        append("JDA ${JDAInfo.VERSION}")
                    })
                inline("Guilds", event.jda.guilds.size)
                inline("Shards", event.jda.shardInfo.shardTotal)
                inline("Invite Me", "https://greemdev.net/invite")
                inline("Support Server", event.client.serverInvite)
                thumbnail(event.jda.selfUser.effectiveAvatarUrl)
            }
        }
    }

}
