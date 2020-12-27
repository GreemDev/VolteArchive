package volte.modules

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte
import volte.database.entities.WelcomeSettings
import volte.meta.getData
import volte.meta.getWelcomeSettings
import volte.meta.then
import java.time.Instant

class WelcomeModule : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val settings = event.guild.getWelcomeSettings()
        if (settings.getDmGreeting().isNotEmpty()) {
            joinGuildDm(event, settings)
        }
        val channelId = settings.getChannel()
        if (channelId.isEmpty()) return
        val channel = event.guild.getTextChannelById(channelId) ?: return
        val greeting = settings.getGreeting()
        if (greeting.isEmpty()) return

        val embed = EmbedBuilder()
            .setColor(settings.getColor())
            .setDescription(settings.replaceGreetingPlaceholders(event.user, event.guild))
            .setThumbnail(event.member.user.effectiveAvatarUrl)
            .setTimestamp(Instant.now())

        channel.sendMessage(embed.build()).queue()
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val settings = event.guild.getWelcomeSettings()
        val channelId = settings.getChannel()
        if (channelId.isEmpty()) return
        val channel = event.guild.getTextChannelById(channelId) ?: return
        val farewell = settings.getFarewell()
        if (farewell.isEmpty()) return

        val embed = EmbedBuilder()
            .setColor(settings.getColor())
            .setDescription(settings.replaceFarewellPlaceholders(event.user, event.guild))
            .setThumbnail(event.user.effectiveAvatarUrl)
            .setTimestamp(Instant.now())

        channel.sendMessage(embed.build()).queue()
    }

    private fun joinGuildDm(event: GuildMemberJoinEvent, settings: WelcomeSettings) {
        event.member.user.openPrivateChannel() then { channel ->
            channel.sendMessage(settings.replacePlaceholders(settings.getDmGreeting(), event.user, event.guild))
        }
    }

}