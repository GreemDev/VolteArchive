package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.Volte
import volte.commands.parsers.Parsers
import volte.meta.categories.operator
import volte.meta.replyInline

class WelcomeCommand : Command() {

    private val opts = arrayListOf("channel", "greeting", "farewell", "color", "dmGreeting")

    init {
        this.name = "welcome"
        this.help =
            "Allows you to get or modify various welcome settings. Valid settings are: [${opts.joinToString(", ")}]"
        this.guildOnly = true
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.replyInline {
                setDescription("You must provide a setting to get/modify. Valid settings are: `${opts.joinToString(", ")}`")
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
                            setDescription("The current Welcome channel for this guild is: <#${settings.getChannel()}>")
                        else
                            setDescription("This guild currently does not have a Welcome channel set.")

                    }
                } else {
                    val parsed = Parsers.channel().parse(event, value)
                    if (parsed != null) {
                        settings.setChannel(parsed.id)
                        event.replyInline {
                            setDescription("Successfully set the Welcome channel to ${parsed.asMention}")
                        }
                    } else {
                        event.replyInline {
                            setDescription("The provided input did not lead to a valid channel. Did you provide the correct name, id, or #mention?")
                        }
                    }
                }
            }
            "greeting" -> {
                if (value == null) {
                    event.replyInline {
                        val greeting = settings.getGreeting()
                        if (greeting.isNotEmpty())
                            setDescription("The current greeting for this guild is: ```\n$greeting```")
                        else
                            setDescription("This guild currently does not have a greeting set.")
                    }
                } else {
                    settings.setGreeting(value)
                    event.replyInline {
                        setDescription("Successfully set the greeting to: ```\n$value```")
                    }
                }
            }
            "farewell" -> {
                if (value == null) {
                    event.replyInline {
                        val farewell = settings.getFarewell()
                        if (farewell.isNotEmpty())
                            setDescription("The current farewell for this guild is: ```\n$farewell```")
                        else
                            setDescription("This guild currently does not have a farewell set.")
                    }
                } else {
                    settings.setFarewell(value)
                    event.replyInline {
                        setDescription("Successfully set the farewell to: ```\n$value```")
                    }
                }
            }
            "color" -> {
                if (value == null) {
                    event.replyInline {
                        val color = settings.getColor()
                        setDescription("The current Welcome color for this guild is: (${color.red}, ${color.green}, ${color.blue})")
                        setColor(color)
                    }
                } else {
                    val parsed = Parsers.color().parse(event, value)
                    if (parsed == null) {
                        event.replyInline {
                            setDescription("Provided color was invalid. Please try an RGB value separated by ;, e.g. `251;0;112`")
                        }
                    } else {
                        settings.setColor("${parsed.red};${parsed.green};${parsed.blue}")
                        event.replyInline {
                            setDescription("Successfully set the color to: (${parsed.red}, ${parsed.green}, ${parsed.blue})")
                            setColor(parsed)
                        }
                    }

                }
            }
            "dmgreeting" -> {
                if (value == null) {
                    event.replyInline {
                        val dmGreeting = settings.getDmGreeting()
                        if (dmGreeting.isNotEmpty())
                            setDescription("The current DM greeting for this guild is: ```\n$dmGreeting```")
                        else
                            setDescription("This guild currently does not have a DM greeting set.")
                    }
                } else {
                    settings.setDmGreeting(value)
                    event.replyInline {
                        setDescription("Successfully set the DM greeting to: ```\n$value```")
                    }
                }
            }
        }
    }
}