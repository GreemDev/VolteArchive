package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.JDAInfo
import volte.meta.*
import volte.meta.categories.utility

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
            addField("Version", Version.formatted(), true)
            addField("Author", "Greem#1337, and contributors on GitHub", true)
            addField(
                "Language/Runtime/Library",
                buildString {
                    appendLine("Kotlin ${KotlinVersion.CURRENT}")
                    appendLine("Java ${try {
                        System.getProperty("java.vm.version").split('+').first()
                    } catch (e: Exception) { System.getProperty("java.version") }}")
                    append("JDA ${JDAInfo.VERSION}")
                }
            )
            addField("Guilds", event.jda.guilds.size, true)
            addField("Shards", event.jda.shardInfo.shardTotal, true)
            addField("Invite Me", "https://greemdev.net/invite", true)
            addField("Support Server", event.client.serverInvite, true)
            setThumbnail(event.jda.selfUser.effectiveAvatarUrl)
        }
    }

}
