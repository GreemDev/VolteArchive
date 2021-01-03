package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.meta.Constants
import volte.meta.replyInline

class NatoCommand : Command() {

    init {
        this.name = "nato"
        this.help = "Translates the given text into the NATO Phonetic Alphabet."
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.replyInline {
                setTitle("Please provide something for me to translate!")
            }
            return
        }

        val arr = event.args.toLowerCase().toCharArray().filter { it != ' ' }

        val list = arrayListOf<String>().apply {
            for (ch in arr) {
                val word = getNato(ch)
                if (word == "ERR") {
                    event.replyInline {
                        setDescription("There is not a NATO word for the character `${ch}`. Only standard English letters and numbers are valid.")
                    }
                    return
                }
                add(word)
            }
        }

        event.replyInline {
            addField("Result", "`${list.joinToString(" ")}`", false)
            addField("Original", "`${event.args}`", false)
        }

    }

    private val natoDefinitions: Map<Char, String> = hashMapOf(
        'a' to "Alfa", 'b' to "Bravo", 'c' to "Charlie", 'd' to "Delta",
        'e' to "Echo", 'f' to "Foxtrot", 'g' to "Golf", 'h' to "Hotel",
        'i' to "India", 'j' to "Juliett", 'k' to "Kilo", 'l' to "Lima",
        'm' to "Mike", 'n' to "November", 'o' to "Oscar", 'p' to "Papa",
        'q' to "Quebec", 'r' to "Romeo", 's' to "Sierra", 't' to "Tango",
        'u' to "Uniform", 'v' to "Victor", 'w' to "Whiskey", 'x' to "X-ray",
        'y' to "Yankee", 'z' to "Zulo", '0' to "Zero", '1' to "One",
        '2' to "Two", '3' to "Three", '4' to "Four", '5' to "Five",
        '6' to "Six", '7' to "Seven", '8' to "Eight", '9' to "Nine"
    )

    private fun getNato(ch: Char): String = natoDefinitions.getOrDefault(ch, "ERR")

}