package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.JDAInfo
import volte.meta.Version
import volte.meta.categories.utility
import volte.meta.replyInline

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
                "Kotlin ${KotlinVersion.CURRENT}\n Java ${
                    System.getProperty("java.vm.version").split("+").first()
                }\n JDA ${JDAInfo.VERSION}",
                false
            )
            addField("Guilds", event.jda.guilds.size.toString(), true)
            addField("Shards", event.jda.shardInfo.shardTotal.toString(), true)
            addField("Invite Me", "https://greemdev.net/invite", true)
            addField("Support Server", event.client.serverInvite, true)
            setThumbnail(event.jda.selfUser.effectiveAvatarUrl)
        }
    }

}
