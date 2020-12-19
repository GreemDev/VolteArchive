package volte.util.obj

import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte

class GuildLoggingManager : ListenerAdapter() {

    fun channel() = Volte.jda().getTextChannelById(Volte.config().guildLogging().channel())!!

    override fun onGuildJoin(event: GuildJoinEvent) {
        if (!Volte.config().guildLogging().enabled()) return
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        if (!Volte.config().guildLogging().enabled()) return
    }

}