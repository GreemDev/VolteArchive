package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.lib.kjda.embed
import volte.lib.meta.*
import volte.lib.meta.categories.utility

class GuildInfoCommand : Command() {

    init {
        this.name = "guildinfo"
        this.aliases = arrayOf("gi", "guildi", "ginfo")
        this.help = "Shows various information about the current guild."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        event.message.reply(embed {
            thumbnail(event.guild.iconUrl)
            title(event.guild.name)
            fields {
                inline("Owner", "<@!${event.guild.ownerId}>")
                inline("Created", event.guild.timeCreated.toInstant().prettyPrint())
                inline("Region", event.guild.region.getName())
                inline("Members", event.guild.memberCount)
                inline("Roles", event.guild.roles.size)
                inline("Channels", event.guild.channels.size)
            }
        }).queue()
    }
}