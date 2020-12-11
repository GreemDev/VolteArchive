package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.meta.Constants

class SetPrefixCommand : Command() {

    init {
        this.name = "setprefix"
        this.help = "Sets the prefix to be used for commands in the guild."
        this.guildOnly = true
        this.category = Constants.operatorCategory()
    }

    override fun execute(event: CommandEvent) {

    }
}