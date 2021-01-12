package volte.lib.kjda

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import volte.meta.createEmbedBuilder
import java.time.temporal.TemporalAccessor

class KEmbedBuilder {

    companion object {
        infix fun of(func: KEmbedBuilder.() -> Unit): KEmbedBuilder = KEmbedBuilder().apply(func)
        infix fun from(event: CommandEvent) = KEmbedBuilder(event.createEmbedBuilder())
        fun from(event: CommandEvent, func: KEmbedBuilder.() -> Unit): KEmbedBuilder = from(event).apply(func)
    }

    private var backing = EmbedBuilder()

    constructor(initial: EmbedBuilder? = null) {
        if (initial != null)
            backing = initial
    }

    constructor(from: MessageEmbed) {
        backing = EmbedBuilder(from)
    }

    fun reset() {
        backing = backing.clear()
    }

    fun length(): Int = backing.length()

    fun field(name: String, body: Any, inline: Boolean = false): KEmbedBuilder {
        backing.addField(name, body.toString(), inline)
        return this
    }

    infix fun description(content: CharSequence?): KEmbedBuilder {
        backing.setDescription(content)
        return this
    }

    infix fun appendDescription(content: CharSequence): KEmbedBuilder {
        backing.appendDescription(content)
        return this
    }

    fun author(name: String, url: String? = null, iconUrl: String? = null): KEmbedBuilder {
        backing.setAuthor(name, url, iconUrl)
        return this
    }

    fun footer(text: String?, iconUrl: String? = null): KEmbedBuilder {
        backing.setFooter(text, iconUrl)
        return this
    }

    infix fun timestamp(temporal: TemporalAccessor): KEmbedBuilder {
        backing.setTimestamp(temporal)
        return this
    }

    infix fun thumbnail(url: String?): KEmbedBuilder {
        backing.setThumbnail(url)
        return this
    }

    fun title(title: String?, url: String? = null): KEmbedBuilder {
        backing.setTitle(title, url)
        return this
    }

    fun build(): MessageEmbed = backing.build()

}