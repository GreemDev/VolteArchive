package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.Volte
import volte.meta.categories.operator
import volte.meta.replyInline

class SetPrefixCommand : Command() {

    init {
        this.name = "prefix"
        this.help = "Shows or sets the prefix to be used for commands in the guild."
        this.guildOnly = true
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        val settings = Volte.db().getSettingsFor(event.guild.id)
        if (event.args.isEmpty()) {
            event replyInline {
                setDescription("The current prefix for this guild is **${settings.getPrefix()}**!")
            }
            return
        }

        settings.setPrefix(event.args)
        event replyInline {
            setDescription("Set the prefix to **${event.args}**!")
        }
    }
}