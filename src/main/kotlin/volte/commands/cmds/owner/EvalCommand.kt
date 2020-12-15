package volte.commands.cmds.owner

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import volte.Volte
import volte.meta.Constants
import volte.meta.stopwatch
import volte.meta.then
import java.awt.Color
import java.util.regex.Pattern
import javax.script.ScriptEngineManager

class EvalCommand : Command() {

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


        val se = ScriptEngineManager().getEngineByName("nashorn").apply {
            put("event", event)
            put("config", Volte.config())
            put("commands", Volte.commands())
            put("runtime", Runtime.getRuntime())
            put("db", Volte.db())
        }

        val builder = EmbedBuilder().addField("Input", "```\n$code```", false)
        event.message.reply(builder.build()).then { message ->
            try {
                var output: Any? = null

                val elapsed = stopwatch {
                    output = se.eval(code)
                }

                if (output == null) {
                    event.reactSuccess()
                    message.delete().queue()
                    return@then
                } else {
                    builder.setTitle("Evaluation Success")
                        .setColor(Color.GREEN)
                        .addField("Output", "```js\n$output```", false)
                        .addField("Type", output!!::class.simpleName, true)
                        .addField("Time", "${elapsed}ms", true)
                    message.editMessage(builder.build()).queue()
                }

            } catch (e: Exception) {
                builder.setTitle("Evaluation Failure")
                    .setColor(Color.RED)
                    .addField("Message", e.message, false)
                    .setDescription("```java\n${e.stackTraceToString()}```")
                message.editMessage(builder.build()).queue()
            }
        }
    }
}