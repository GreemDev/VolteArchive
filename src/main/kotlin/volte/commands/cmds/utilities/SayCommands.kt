package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import volte.meta.Constants
import volte.meta.createEmbed
import volte.meta.getHighestRoleWithColor
import java.awt.Color

class SayCommand : Command() {

    init {
        this.name = "say"
        this.help = "Bot repeats what you tell it to."
        this.category = Constants.utilityCategory()
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
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        event.reply(EmbedBuilder().setColor(event.member.getHighestRoleWithColor()?.color ?: Color.CYAN).setDescription(event.args).build()) {
            event.message.delete().queue()
        }
    }
}