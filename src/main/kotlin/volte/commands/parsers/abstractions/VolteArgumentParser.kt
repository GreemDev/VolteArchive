package volte.commands.parsers.abstractions

import com.jagrosh.jdautilities.command.CommandEvent

abstract class VolteArgumentParser<T> {

    abstract fun parse(event: CommandEvent, value: String): T
}