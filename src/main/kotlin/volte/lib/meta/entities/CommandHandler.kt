package volte.lib.meta.entities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.CommandListener
import net.dv8tion.jda.api.entities.ChannelType
import volte.Volte
import java.time.Instant

class CommandHandler : CommandListener {

    override fun onCommand(event: CommandEvent, command: Command?) {
        if (event.isFromType(ChannelType.PRIVATE)) return

        if (command == null) return

        Volte logger {
            info(buildString {
                appendLine("|  -Command from user: ${event.member.user.asTag}")
                appendLine("${spaces}|    -Message Content: ${event.message.contentRaw}")
                appendLine("${spaces}|           -In Guild: ${event.message.guild}")
                appendLine("${spaces}|         -In Channel: ${event.textChannel}")
                appendLine("${spaces}|        -Time Issued: ${Instant.now()}")
            })
        }
    }

    private val spaces = "                               "

}