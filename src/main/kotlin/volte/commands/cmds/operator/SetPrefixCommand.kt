package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.Volte
import volte.lib.meta.categories.operator
import volte.lib.meta.replyInline

class SetPrefixCommand : Command() {

    init {
        this.name = "prefix"
        this.help = "Shows or sets the prefix to be used for commands in the guild."
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        val settings = Volte.db().getSettingsFor(event.guild.id)
        if (event.args.isEmpty()) {
            event replyInline {
                description("The current prefix for this guild is **${settings.getPrefix()}**!")
            }
            return
        }

        settings.setPrefix(event.args)
        event replyInline {
            description("Set the prefix to **${event.args}**!")
        }
    }
}