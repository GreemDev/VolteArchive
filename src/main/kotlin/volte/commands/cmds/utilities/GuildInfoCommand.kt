package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.meta.*
import volte.meta.categories.utility

class GuildInfoCommand : Command() {

    init {
        this.name = "guildinfo"
        this.aliases = arrayOf("gi", "guildi", "ginfo")
        this.help = "Shows various information about the current guild."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        event replyInline {
            setThumbnail(event.guild.iconUrl)
            setTitle(event.guild.name)
            addField("Owner", "<@!${event.guild.ownerId}>", true)
            addField("Created", event.guild.timeCreated.toInstant().prettyPrint(), true)
            addField("Region", event.guild.region.getName(), true)
            addField("Members", event.guild.memberCount, true)
            addField("Roles", event.guild.roles.size, true)
            addField("Channels", event.guild.channels.size, true)
        }
    }
}