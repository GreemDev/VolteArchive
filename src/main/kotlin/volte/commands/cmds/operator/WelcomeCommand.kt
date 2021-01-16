package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.TextChannel
import volte.Volte
import volte.commands.parsers.Parsers
import volte.lib.meta.categories.operator
import volte.lib.meta.replyInline
import java.awt.Color

class WelcomeCommand : Command() {

    private val opts = arrayListOf("channel", "greeting", "farewell", "color", "dmGreeting").joinToString(", ")

    init {
        this.name = "welcome"
        this.help =
            "Allows you to get or modify various welcome settings. Valid settings are: [$opts]"
        this.guildOnly = true
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.replyInline {
                description("You must provide a setting to get/modify. Valid settings are: `$opts}`")
            }
            return
        }

        val settings = Volte.db().getWelcomeSettingsFor(event.guild.id)
        val argArr = event.args.split(" ")
        val option = argArr.first()
        val value: String? = if (argArr.size > 1) argArr.subList(1, argArr.size).joinToString(" ") else null
        when (option.toLowerCase()) {
            "channel" -> {
                if (value == null) {
                    event.replyInline {
                        val chan = settings.getChannel()
                        if (chan.isNotEmpty())
                            description("The current Welcome channel for this guild is: <#${settings.getChannel()}>")
                        else
                            description("This guild currently does not have a Welcome channel set.")

                    }
                } else {
                    val parsed = Parsers.parse<TextChannel>(event, value)
                    if (parsed != null) {
                        settings.setChannel(parsed.id)
                        event.replyInline {
                            description("Successfully set the Welcome channel to ${parsed.asMention}")
                        }
                    } else {
                        event.replyInline {
                            description("The provided input did not lead to a valid channel. Did you provide the correct name, id, or #mention?")
                        }
                    }
                }
            }
            "greeting" -> {
                if (value == null) {
                    event.replyInline {
                        val greeting = settings.getGreeting()
                        if (greeting.isNotEmpty())
                            description("The current greeting for this guild is: ```\n$greeting```")
                        else
                            description("This guild currently does not have a greeting set.")
                    }
                } else {
                    settings.setGreeting(value)
                    event.replyInline {
                        description("Successfully set the greeting to: ```\n$value```")
                    }
                }
            }
            "farewell" -> {
                if (value == null) {
                    event.replyInline {
                        val farewell = settings.getFarewell()
                        if (farewell.isNotEmpty())
                            description("The current farewell for this guild is: ```\n$farewell```")
                        else
                            description("This guild currently does not have a farewell set.")
                    }
                } else {
                    settings.setFarewell(value)
                    event.replyInline {
                        description("Successfully set the farewell to: ```\n$value```")
                    }
                }
            }
            "color" -> {
                if (value == null) {
                    event.replyInline {
                        val color = settings.getColor()
                        description("The current Welcome color for this guild is: (${color.red}, ${color.green}, ${color.blue})")
                        color(color)
                    }
                } else {
                    val parsed = Parsers.parse<Color>(event, value)
                    if (parsed == null) {
                        event.replyInline {
                            description("Provided color was invalid. Please try an RGB value separated by ;, e.g. `251;0;112`")
                        }
                    } else {
                        settings.setColor("${parsed.red};${parsed.green};${parsed.blue}")
                        event.replyInline {
                            description("Successfully set the color to: (${parsed.red}, ${parsed.green}, ${parsed.blue})")
                            color(parsed)
                        }
                    }

                }
            }
            "dmgreeting" -> {
                if (value == null) {
                    event.replyInline {
                        val dmGreeting = settings.getDmGreeting()
                        if (dmGreeting.isNotEmpty())
                            description("The current DM greeting for this guild is: ```\n$dmGreeting```")
                        else
                            description("This guild currently does not have a DM greeting set.")
                    }
                } else {
                    settings.setDmGreeting(value)
                    event.replyInline {
                        description("Successfully set the DM greeting to: ```\n$value```")
                    }
                }
            }
            else -> {
                event.replyInline {
                    title("Invalid setting provided.")
                    description("Valid settings to show or modify are: [$opts]")
                }
            }
        }
    }
}