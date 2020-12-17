package volte.modules

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.meta.getData
import volte.meta.then
import volte.util.DiscordUtil
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class AntilinkModule : ListenerAdapter() {

    companion object {
        private val pattern: Pattern = Pattern.compile("discord(?:\\.gg|\\.io|\\.me|app\\.com/invite)/([\\w\\-]+)")
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!event.guild.getData().getAntilink() || DiscordUtil.isOperator(event.member!!)) return

        val matcher = pattern.matcher(event.message.contentRaw)
        if (matcher.matches()) {
            event.message.delete().reason("Contained an invite link.").queue()
            event.guild.retrieveMember(event.author) then { member ->
                event.channel.sendMessage(DiscordUtil.createDefaultEmbed("${member.asMention}, don't send invite links here!", member).build()) then { message ->
                    message.delete().queueAfter(5, TimeUnit.SECONDS)
                }
            }
        }
    }


}