package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.lib.meta.replyInline
import volte.lib.meta.then

class ChannelNameCommand : Command() {
    override fun execute(event: CommandEvent) {
        event.textChannel.manager.setName(
            event.args.replace("  ", "_").replace(" ", "-")
        ) then {
            event replyInline {
                description("Set this channel's name to ${event.args.replace(" ", "-")}")
            }
        }
    }
}