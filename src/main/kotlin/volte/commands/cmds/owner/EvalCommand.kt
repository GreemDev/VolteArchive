package volte.commands.cmds.owner

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import volte.meta.categories.owner
import volte.meta.*
import volte.Volte
import java.awt.Color
import java.util.regex.Pattern
import javax.script.ScriptEngineManager
import kotlin.system.measureTimeMillis

class EvalCommand : Command() {

    init {
        this.name = "eval"
        this.help = "Evaluates Nashorn code with predefined variables for you to use."
        this.category = owner()
    }

    private val pattern = Pattern.compile("[\\s\\t\\n\\r]*`{3}(?:js)?[\\n\\r]+((?:.|\\n|\\t\\r)+)`{3}")

    override fun execute(event: CommandEvent) {
        val code = with(pattern.matcher(event.args)) {
            if (matches()) {
                group(1)
            } else event.args
        }

        val se = ScriptEngineManager().getEngineByName("js").apply {
            put("event", event)
            put("config", Volte.config())
            put("commands", Volte.commands())
            put("runtime", Runtime.getRuntime())
            put("db", Volte.db())
            put("conn", Volte.db().connector())
        }

        event replyInline {
            addField("Input", "```\n$code```")
        } then { message ->
            val builder = EmbedBuilder(message.embeds.first())
            try {
                var output: Any?

                val elapsed = measureTimeMillis {
                    output = se.eval(code)
                }

                if (output == null) {
                    event.reactSuccess()
                    message.delete().queue()
                } else {
                    builder.setTitle("Evaluation Success")
                        .setColor(Color.GREEN)
                        .addField("Output", "```js\n$output```")
                        .addField("Type", output!!::class.simpleName, true)
                        .addField("Time", "${elapsed}ms", true)
                    message.editMessage(builder.build()).reference(event.message).queue()
                }

            } catch (e: Exception) {
                builder.setTitle("Evaluation Failure")
                    .setColor(Color.RED)
                    .setDescription("```js\n${e.message}```")
                message.editMessage(builder.build()).reference(event.message).queue()
            }
        }
    }
}