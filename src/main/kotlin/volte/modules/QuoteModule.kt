package volte.modules

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte
import volte.meta.then
import volte.meta.DiscordUtil
import volte.meta.addField
import java.util.regex.Matcher
import java.util.regex.Pattern

class QuoteModule : ListenerAdapter() {

    companion object {
        private val linkPattern: Pattern =
            Pattern.compile("(?<preComment>\\S+\\s+\\S*)?https?://(?:(?:ptb|canary)\\.)?discord(app)?\\.com/channels/(?<guild>\\d+)/(?<channel>\\d+)/(?<message>\\d+)/?(?<postComment>\\S*\\s+\\S+)?")
        private val linkRemover: Pattern =
            Pattern.compile("https?://(?:(?:ptb|canary)\\.)?discord(app)?\\.com/channels/(\\d+)/(\\d+)/(\\d+)?")
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!Volte.db().getSettingsFor(event.guild.id).getAutoQuote() or event.author.isBot) return

        with(linkPattern.matcher(event.message.contentRaw)) {
            if (!matches()) return

            val guildId = group("guild")
            val channelId = group("channel")
            val messageId = group("message")

            val g = event.jda.getGuildById(guildId)
            val c = g?.getTextChannelById(channelId) ?: return

            c.retrieveMessageById(messageId) then { message ->
                event.channel.sendMessage(generateQuoteEmbed(message, event, this)) then {
                    event.message.delete().queue()
                }
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

        if (!matcher.group("preComment").isNullOrEmpty() or !matcher.group("postComment").isNullOrEmpty()) {
            with (linkRemover.matcher(event.message.contentRaw)) {
                var strings = replaceAll("|").split(" ")

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
                e.addField("Comment", strings.joinToString(" "))
            }
        }

        e.addField("Original Message", "[Click here](${message.jumpUrl})")

        return e.build()
    }

}