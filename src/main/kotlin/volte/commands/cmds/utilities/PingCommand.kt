package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import volte.meta.Emoji
import volte.meta.stopwatch
import java.awt.Color

class PingCommand : Command() {

    init {
        this.name = "ping"
        this.help = "Simple command to check API latency and gateway ping."
    }

    override fun execute(event: CommandEvent) {
        lateinit var message: Message
        val embed = EmbedBuilder()
            .setColor(Color.CYAN).setDescription("${Emoji.OK_HAND} **Gateway**: ${event.jda.gatewayPing}ms\n")

        val apiLatency = stopwatch {
            message = event.textChannel.sendMessage(embed.build()).complete()
        }

        message.editMessage(embed.appendDescription("${Emoji.CLAP} **REST**: ${apiLatency}ms").build()).queue()
    }
}