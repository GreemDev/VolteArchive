package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.meta.replyInline
import volte.meta.then

class ChannelNameCommand : Command() {
    override fun execute(event: CommandEvent) {
        event.textChannel.manager.setName(
            event.args.replace("  ", "_").replace(" ", "-")
        ) then {
            event replyInline {
                setDescription("Set this channel's name to ${event.args.replace(" ", "-")}")
            }
        }
    }
}