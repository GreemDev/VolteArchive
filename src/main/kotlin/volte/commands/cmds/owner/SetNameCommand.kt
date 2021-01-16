package volte.commands.cmds.owner

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.lib.meta.categories.owner
import volte.lib.meta.replyInline
import volte.lib.meta.then

class SetNameCommand : Command() {

    init {
        this.name = "setname"
        this.help = "Sets the name of the currently logged in bot."
        this.ownerCommand = true
        this.category = owner()
    }

    override fun execute(event: CommandEvent) {
        event.jda.selfUser.manager.setName(event.args) then {
            event.replyInline {
                description("My username is now **${event.args}#${event.jda.selfUser.discriminator}**.")
            }
        }
    }
}