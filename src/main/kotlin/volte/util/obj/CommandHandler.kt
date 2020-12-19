package volte.util.obj

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.CommandListener
import volte.Volte
import java.time.Instant

class CommandHandler : CommandListener {

    override fun onCommand(event: CommandEvent, command: Command) {
        val sb = StringBuilder()
            .appendLine("|  -Command from user: ${event.member.user.asTag}")
            .appendLine("${spaces}|    -Message Content: ${event.message.contentRaw}")
            .appendLine("${spaces}|           -In Guild: ${event.message.guild}")
            .appendLine("${spaces}|         -In Channel: ${event.textChannel}")
            .appendLine("${spaces}|        -Time Issued: ${Instant.now()}")

        Volte.logger().info(sb.toString())
    }

    private val spaces = "                               "

}