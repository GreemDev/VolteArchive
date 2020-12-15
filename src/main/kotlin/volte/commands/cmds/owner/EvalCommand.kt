package volte.commands.cmds.owner

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import volte.Volte
import volte.database.VolteDatabase
import volte.meta.Constants
import volte.meta.then
import java.util.regex.Pattern
import javax.script.ScriptEngineManager

class EvalCommand(private val volte: Volte) : Command() {

    init {
        this.name = "eval"
        this.help = "Evaluates Nashorn code with predefined variables for you to use."
        this.category = Constants.ownerCategory()
    }

    private val pattern = Pattern.compile("[\\s\\t\\n\\r]*`{3}(?:js)?[\\n\\r]+((?:.|\\n|\\t\\r)+)`{3}")

    override fun execute(event: CommandEvent) {
        var code = event.args
        val matcher = pattern.matcher(code)
        if (matcher.matches()) {
            code = matcher.group(1)
        }



        /*val se = ScriptEngineManager().getEngineByName("nashorn").apply {
            put("event", event)
            put("config", Volte.config())
            put("commands", Volte.commands())
            put("runtime", Runtime.getRuntime())
            put("db", Volte.db())
        }*/

        val builder = EmbedBuilder().addField("Input", "```\n$code```", false)
        event.message.reply(builder.build()).then {
            try {

            } catch (e: Exception) {

            }
        }
    }
}