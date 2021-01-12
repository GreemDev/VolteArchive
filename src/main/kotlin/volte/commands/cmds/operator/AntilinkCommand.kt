package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.Volte
import volte.commands.parsers.Parsers
import volte.meta.categories.operator
import volte.meta.replyEmbedInline

class AntilinkCommand : Command() {

    init {
        this.name = "antilink"
        this.help = "Shows or sets the option for toggling the Antilink system."
        this.guildOnly = true
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        val data = Volte.db().getSettingsFor(event.guild.id)

        if (event.args.isEmpty()) {
            event.replyEmbedInline("The Antilink system is currently ${if (data.getAntilink()) "enabled" else "disabled"}} for this guild.")
            return
        }

        val result = Parsers.parse<Boolean>(event, event.args)
        if (result == null) {
            event.replyEmbedInline("You didn't provide a valid boolean value. Try `true` or `false` next time!")
        } else {
            data.setAntilink(result)

            event.replyEmbedInline("Successfully ${if (result) "enabled" else "disabled"} the Antilink system for this guild.")
        }
    }
}