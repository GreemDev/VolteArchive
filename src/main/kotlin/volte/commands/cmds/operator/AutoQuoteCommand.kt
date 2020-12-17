package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.Volte
import volte.commands.parsers.Parsers
import volte.meta.Constants
import volte.meta.createEmbed

class AutoQuoteCommand : Command() {

    init {
        this.name = "autoquote"
        this.help = "Shows or sets the option for toggling the Quote module."
        this.guildOnly = true
        this.category = Constants.operatorCategory()
    }

    override fun execute(event: CommandEvent) {
        val data = Volte.db().getSettingsFor(event.guild.id)
        val autoquote = data.getAutoQuote()

        if (event.args.isEmpty()) {
            event.message.reply(event.createEmbed("The AutoQuote system is currently ${if (autoquote) "enabled" else "disabled"}} for this guild."))
            return
        }

        val result = Parsers.boolean().parse(event, event.args)
        if (result == null) {
            event.message.reply(event.createEmbed("You didn't provide a valid boolean value. Try `true` or `false` next time!")).queue()
        } else {
            data.setAutoQuote(result)

            event.message.reply(event.createEmbed("Successfully ${if (result) "enabled" else "disabled"} the AutoQuote system for this guild.")).queue()
        }
    }

}