package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.internal.utils.EncodingUtil
import volte.lib.kjda.embed
import volte.lib.meta.categories.utility

class BigEmojiCommand : Command() {

    init {
        this.name = "bigemoji"
        this.help = "Shows a massive version of whatever emoji you give it. Limit 1"
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.message.reply(embed {
                title("You need to provide an emote.")
            }).queue()
            return
        }

        if (event.message.emotes.isNotEmpty()) {
            event.message.reply(embed {
                description(event.message.emotes.first().imageUrl)
                image(event.message.emotes.first().imageUrl)
            }).queue()
            return
        }

        val codepoints = EncodingUtil.encodeCodepoints(event.args)
        if (!codepoints.startsWith("U+", true)) {
            event.message.reply(embed {
                title("Please provide a valid emoji!")
            }).queue()
            return
        }

        event.message.reply(embed {
            image("https://i.kuro.mu/emoji/1024x1024/${codepoints.substring(2)}.png")
            description("https://i.kuro.mu/emoji/1024x1024/${codepoints.substring(2)}.png")
        }).queue()
    }
}