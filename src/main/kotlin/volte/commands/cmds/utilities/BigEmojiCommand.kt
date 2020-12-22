package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.internal.utils.EncodingUtil
import volte.meta.Constants
import volte.meta.messageReply

class BigEmojiCommand : Command() {

    init {
        this.name = "bigemoji"
        this.help = "Shows a massive version of whatever emoji you give it. Limit 1"
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.messageReply {
                setTitle("You need to provide an emote.")
            }
            return
        }

        if (event.message.emotes.isNotEmpty()) {
            event.messageReply {
                setImage(event.message.emotes.first().imageUrl)
            }
            return
        }

        val codepoints = EncodingUtil.encodeCodepoints(event.args)
        if (!codepoints.startsWith("U+", true)) {
            event.messageReply {
                setTitle("Please provide a valid emoji!")
            }
            return
        }

        event.messageReply {
            setImage("https://i.kuro.mu/emoji/1024x1024/${codepoints.substring(2)}.png")
        }
    }
}