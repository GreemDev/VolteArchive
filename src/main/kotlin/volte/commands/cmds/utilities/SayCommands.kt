package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.lib.meta.categories.utility
import volte.lib.meta.createEmbed
import volte.lib.meta.getHighestRoleWithColor
import volte.lib.meta.reply
import java.awt.Color

class SayCommand : Command() {

    init {
        this.name = "say"
        this.help = "Bot repeats what you tell it to."
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        event.reply(event.createEmbed(event.args))
    }
}

class SilentSayCommand : Command() {

    init {
        this.name = "silentsay"
        this.aliases = arrayOf("ssay")
        this.help = "Bot repeats what you tell it to with no embed author."
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        event.reply {
            setColor(event.member.getHighestRoleWithColor().valueOrNull()?.color ?: Color.CYAN)
            setDescription(event.args)
        } then {
            event.message.delete().queue()
        }
    }
}