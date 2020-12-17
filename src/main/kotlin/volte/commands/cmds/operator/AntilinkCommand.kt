package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.Volte
import volte.commands.parsers.Parsers
import volte.meta.Constants
import volte.meta.createEmbed

class AntilinkCommand : Command() {

    init {
        this.name = "antilink"
        this.help = "Shows or sets the option for toggling the Antilink system."
        this.guildOnly = true
        this.category = Constants.operatorCategory()
    }

    override fun execute(event: CommandEvent) {
        val data = Volte.db().getSettingsFor(event.guild.id)
        val antilink = data.getAntilink()

        if (event.args.isEmpty()) {
            event.message.reply(event.createEmbed("The Antilink system is currently ${if (antilink) "enabled" else "disabled"}} for this guild."))
            return
        }

        val result = Parsers.boolean().parse(event, event.args)
        if (result == null) {
            event.message.reply(event.createEmbed("You didn't provide a valid boolean value. Try `true` or `false` next time!")).queue()
        } else {
            data.setAntilink(result)

            event.message.reply(event.createEmbed("Successfully ${if (result) "enabled" else "disabled"} the Antilink system for this guild.")).queue()
        }
    }
}