package volte.modules

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte
import volte.meta.then
import volte.util.DiscordUtil
import java.util.regex.Matcher
import java.util.regex.Pattern

class QuoteModule : ListenerAdapter() {

    companion object {
        val linkPattern =
            Pattern.compile("(?<Prelink>\\S+\\s+\\S*)?https?://(?:(?:ptb|canary)\\.)?discord(app)?\\.com/channels/(?<GuildId>\\d+)/(?<ChannelId>\\d+)/(?<MessageId>\\d+)/?(?<Postlink>\\S*\\s+\\S+)?")
        val linkRemover =
            Pattern.compile("https?://(?:(?:ptb|canary)\\.)?discord(app)?\\.com/channels/(\\d+)/(\\d+)/(\\d+)?")
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!Volte.db().getSettingsFor(event.guild.id).getAutoQuote()) return

        val matcher = linkPattern.matcher(event.message.contentRaw)
        if (!matcher.matches()) return

        val guildId = matcher.group("GuildId")
        val channelId = matcher.group("ChannelId")
        val messageId = matcher.group("MessageId")

        val g = event.jda.getGuildById(guildId)
        val c = g?.getTextChannelById(channelId) ?: return

        c.retrieveMessageById(messageId) then { message ->
            event.channel.sendMessage(generateQuoteEmbed(message, event, matcher)) then {
                event.message.delete().queue()
            }
        }
    }

    private fun generateQuoteEmbed(message: Message, event: GuildMessageReceivedEvent, matcher: Matcher): MessageEmbed {
        val e = DiscordUtil.createDefaultEmbed(member = event.member!!)
            .setAuthor(message.author.name, null, message.author.effectiveAvatarUrl)
            .setFooter("Quoted by ${event.author.asTag}", event.author.effectiveAvatarUrl)

        if (message.contentRaw.isNotEmpty()) {
            e.setDescription(message.contentRaw)
        }

        if (message.contentRaw.isEmpty() && message.attachments.size > 0) {
            if (message.attachments.first().isImage) {
                e.setImage(message.attachments.first().url)
            }
        }

        if (message.contentRaw.isNotEmpty() && message.attachments.size > 0) {
            e.setDescription(message.contentRaw)
            if (message.attachments.first().isImage) {
                e.setImage(message.attachments.first().url);
            }
        }

        if (!matcher.group("Prelink").isNullOrEmpty() or !matcher.group("Postlink").isNullOrEmpty()) {
            val match = linkRemover.matcher(event.message.contentRaw)
            var strings = match.replaceAll("|").split(" ")

            if (strings.size == 2) {
                strings = strings.map {
                    if (it != "") {
                        if (it.endsWith("|") or it.startsWith("|")) {
                            return@map it.replace("|", "")
                        }
                        return@map it
                    }
                    return@map ""
                }.filter { it != "" }
            }
            e.addField("Comment", strings.joinToString(" "), false)
        }

        e.addField("Original Message", "[Click here](${message.jumpUrl})", false)

        return e.build()
    }

}