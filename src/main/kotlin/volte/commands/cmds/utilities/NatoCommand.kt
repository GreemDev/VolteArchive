package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.lib.meta.categories.utility
import volte.lib.meta.replyInline

class NatoCommand : Command() {

    init {
        this.name = "nato"
        this.help = "Translates the given text into the NATO Phonetic Alphabet."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.replyInline {
                title("Please provide something for me to translate!")
            }
            return
        }

        val arr = event.args.toLowerCase().replace(' ', '\n')

        val list = arrayListOf<String>().apply {
            for (ch in arr) {
                if (ch == '\n') {
                    add("`\n`")
                } else {
                    val word = natoDefinitions[ch]
                    if (word == null) {
                        event.replyInline {
                            description("There is not a NATO word for the character `${ch}`. Only standard English letters and numbers are valid.")
                        }
                        return
                    }
                    add(word)
                }
            }
        }

        event.replyInline {
            fields {
                normal("Result", "`${list.joinToString(" ")}`")
                normal("Original", "`${event.args}`")
            }
        }

    }

    private val natoDefinitions: Map<Char, String> = hashMapOf(
        'a' to "Alfa", 'b' to "Bravo", 'c' to "Charlie",
        'd' to "Delta", 'e' to "Echo", 'f' to "Foxtrot",
        'g' to "Golf", 'h' to "Hotel", 'i' to "India",
        'j' to "Juliett", 'k' to "Kilo", 'l' to "Lima",
        'm' to "Mike", 'n' to "November", 'o' to "Oscar",
        'p' to "Papa", 'q' to "Quebec", 'r' to "Romeo",
        's' to "Sierra", 't' to "Tango", 'u' to "Uniform",
        'v' to "Victor", 'w' to "Whiskey", 'x' to "X-ray",
        'y' to "Yankee", 'z' to "Zulo", '0' to "Zero",
        '1' to "One", '2' to "Two", '3' to "Three",
        '4' to "Four", '5' to "Five", '6' to "Six",
        '7' to "Seven", '8' to "Eight", '9' to "Nine"
    )
}