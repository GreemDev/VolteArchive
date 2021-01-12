package volte.meta.entities

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte
import volte.meta.*
import java.awt.Color
import java.time.Instant

class GuildLoggingManager : ListenerAdapter() {

    companion object {
        val instance = GuildLoggingManager()
    }

    fun channel() = Volte.jda().getTextChannelById(Volte.config().guildLogging().channel())

    override fun onGuildJoin(event: GuildJoinEvent) {
        if (!Volte.config().guildLogging().enabled()) return
        val chan = channel() ?: return
        val e = newEmbed {
            setTimestamp(Instant.now())
            setColor(event.guild.owner?.getHighestRoleWithColor()?.valueOrNull()?.color ?: Color.GREEN)
            setTitle("Added to a guild")
            addField("Name", event.guild.name, true)
            addField("Members", event.guild.memberCount, true)
        }

        if (event.guild.owner != null) {
            e.setAuthor(event.guild.owner!!.effectiveName, null, event.guild.owner!!.user.effectiveAvatarUrl)
        }

        event.guild.loadMembers().onSuccess {
            var users = 0
            var bots = 0

            for (member in it) {
                if (member.user.isBot) {
                    bots++
                } else {
                    users++
                }
            }

            if (bots > users) {
                chan.sendMessage("<@${Volte.config().owner()}>: Joined a guild with more bots than users. Bot farm?")
                e.setColor(Color.RED)
            }

            chan.sendMessage(e.apply {
                addField("Bots", bots, true)
                addField("Users", users, true)
            }.build())

        }
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        if (!Volte.config().guildLogging().enabled()) return
        val chan = channel() ?: return
        val e = newEmbed {
            setTimestamp(Instant.now())
            setColor(Color.ORANGE)
            setTitle("Removed from/Left a Guild")
            addField("Name", event.guild.name, true)
            addField("Members", event.guild.memberCount, true)
        }

        if (event.guild.owner != null) {
            e.setAuthor(event.guild.owner!!.effectiveName, null, event.guild.owner!!.user.effectiveAvatarUrl)
        }

        chan.sendMessage(e.build()).queue()

    }

    fun onError(throwable: Throwable) {
        if (!Volte.config().guildLogging().enabled()) return
        val chan = channel() ?: return

        chan.sendMessage(buildEmbed {
            setTimestamp(Instant.now())
            setColor(Color.MAGENTA)
            setTitle("Internal Volte Error")
            addField("Type", throwable::class.java.simpleName, true)
            addField("Message", throwable.message, true)
            setDescription("```java\n${throwable.stackTraceToString()}```")
        }).queue()
    }

}