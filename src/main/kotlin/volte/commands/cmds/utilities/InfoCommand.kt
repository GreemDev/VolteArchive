package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.JDAInfo
import volte.meta.createEmbedBuilder
import volte.meta.messageReply
import volte.meta.reply

class InfoCommand : Command() {

    init {
        this.name = "info"
        this.aliases = arrayOf("botinfo")
        this.help = "Shows various information about Volte."
        this.guildOnly = true
    }

    override fun execute(event: CommandEvent) {
        event.messageReply {
            addField("Version", "4.0.0.0", true)
            addField("Author", "Greem#1337, and contributors on GitHub", true)
            addField("Language/Runtime/Library", "Kotlin ${KotlinVersion.CURRENT}, JVM ${System.getenv("java.version")} JDA ${JDAInfo.VERSION}", false)
            addField("Guilds", event.jda.guilds.size.toString(), true)
            addField("Shards", event.jda.shardInfo.shardTotal.toString(), true)
            addField("Invite Me", "https://greemdev.net/invite", true)
            setThumbnail(event.jda.selfUser.effectiveAvatarUrl)
        }
    }

}
