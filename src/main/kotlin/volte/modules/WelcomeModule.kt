package volte.modules

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte
import volte.database.WelcomeSettings
import volte.meta.then
import java.awt.Color
import java.time.Instant

class WelcomeModule : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val settings = Volte.db().getWelcomeSettingsFor(event.guild.id)
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
            .setDescription(replacePlaceholders(greeting, event.user, event.guild))
            .setThumbnail(event.member.user.effectiveAvatarUrl)
            .setTimestamp(Instant.now())

        channel.sendMessage(embed.build()).queue()
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val settings = Volte.db().getWelcomeSettingsFor(event.guild.id)
        val channelId = settings.getChannel()
        if (channelId.isEmpty()) return
        val channel = event.guild.getTextChannelById(channelId) ?: return
        val farewell = settings.getFarewell()
        if (farewell.isEmpty()) return

        val embed = EmbedBuilder()
            .setColor(settings.getColor())
            .setDescription(replacePlaceholders(farewell, event.user, event.guild))
            .setThumbnail(event.user.effectiveAvatarUrl)
            .setTimestamp(Instant.now())

        channel.sendMessage(embed.build()).queue()
    }

    private fun joinGuildDm(event: GuildMemberJoinEvent, settings: WelcomeSettings) {
        event.member.user.openPrivateChannel() then { channel ->
            channel.sendMessage(replacePlaceholders(settings.getDmGreeting(), event.user, event.guild))
        }
    }

    private fun replacePlaceholders(text: String, user: User, guild: Guild): String {
        return text.replace("{GuildName}", guild.name, true)
            .replace("{MemberName}", user.name, true)
            .replace("{MemberMention}", user.asMention, true)
            .replace("{OwnerMention}", "<@${guild.ownerId}>", true)
            .replace("{MemberTag}", user.discriminator, true)
            .replace("{MemberCount}", "${guild.memberCount}", true)
            .replace("{MemberString}", user.asTag)
    }

}