package volte.commands.cmds.owner

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import volte.lib.meta.categories.owner
import volte.lib.meta.*
import volte.Volte
import volte.lib.kjda.KEmbedBuilder
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
            put("conn", Volte.db().connection())
        }

        event replyInline {
            fields {
                normal("Input", "```\n$code```")
            }
        } then { message ->
            val builder = KEmbedBuilder(EmbedBuilder(message.embeds.first()))
            try {
                var output: Any?

                val elapsed = measureTimeMillis {
                    output = se.eval(code)
                }

                if (output == null) {
                    event.reactSuccess()
                    message.delete().queue()
                } else {
                    builder.title("Evaluation Success")
                        .color(Color.GREEN)
                        .fields {
                            normal("Output", "```js\n$output```")
                            inline("Type", output!!::class.java.simpleName)
                            inline("Time", "${elapsed}ms")
                        }
                    message.editMessage(builder.build()).reference(event.message).queue()
                }

            } catch (e: Exception) {
                builder.title("Evaluation Failure")
                    .color(Color.RED)
                    .description("```js\n${e.message}```")
                message.editMessage(builder.build()).reference(event.message).queue()
            }
        }
    }
}