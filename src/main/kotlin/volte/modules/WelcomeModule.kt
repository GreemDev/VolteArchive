package volte.modules

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte
import volte.database.VolteDatabase
import volte.database.WelcomeSettings
import java.awt.Color
import java.sql.ResultSet
import java.time.Instant

class WelcomeModule : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val settings = Volte.db().getWelcomeSettingsFor(event.guild.id)
        if (settings.dmGreeting().isNotEmpty()) {
            joinGuildDm(event, settings)
        }
        if (settings.channel().isEmpty()) return
        val channel = event.guild.getTextChannelById(settings.channel()) ?: return
        if (settings.greeting().isEmpty()) return

        val embed = EmbedBuilder()
            .setColor(settings.color())
            .setDescription(replacePlaceholders(settings.greeting(), event.user, event.guild))
            .setThumbnail(event.member.user.effectiveAvatarUrl)
            .setTimestamp(Instant.now())

        channel.sendMessage(embed.build()).queue()
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val settings = Volte.db().getWelcomeSettingsFor(event.guild.id)
        if (settings.channel().isEmpty()) return
        val channel = event.guild.getTextChannelById(settings.channel())
        if (settings.farewell().isEmpty() || channel == null) return

        val embed = EmbedBuilder()
            .setColor(settings.color())
            .setDescription(replacePlaceholders(settings.farewell(), event.user, event.guild))
            .setThumbnail(event.user.effectiveAvatarUrl)
            .setTimestamp(Instant.now())

        channel.sendMessage(embed.build()).queue()
    }

    private fun joinGuildDm(event: GuildMemberJoinEvent, settings: WelcomeSettings) {
        event.member.user.openPrivateChannel().queue {
            it.sendMessage(replacePlaceholders(settings.dmGreeting(), event.user, event.guild))
        }
    }

    companion object {
        fun parseColor(colorStr: String): Color {
            val split = colorStr.split(";")
            val r = split[0].toFloat()
            val g = split[1].toFloat()
            val b = split[2].toFloat()
            return Color(r, g, b)
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