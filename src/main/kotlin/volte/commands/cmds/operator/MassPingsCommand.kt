package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.Volte
import volte.commands.parsers.Parsers
import volte.meta.categories.operator
import volte.meta.createEmbed

class MassPingsCommand : Command() {

    init {
        this.name = "masspings"
        this.help = "Shows or sets the option for toggling the Mass Ping system."
        this.guildOnly = true
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        val data = Volte.db().getSettingsFor(event.guild.id)
        val massPings = data.getMassPings()

        if (event.args.isEmpty()) {
            event.message.reply(event.createEmbed("The Mass Ping system is currently ${if (massPings) "enabled" else "disabled"} for this guild."))
                .queue()
            return
        }

        val result = Parsers.boolean().parse(event, event.args)
        if (result == null) {
            event.message.reply(event.createEmbed("You didn't provide a valid boolean value. Try `true` or `false` next time!"))
                .queue()
        } else {
            data.setMassPings(result)

            event.message.reply(event.createEmbed("Successfully ${if (result) "enabled" else "disabled"} the Mass Ping system for this guild."))
                .queue()
        }
    }
}