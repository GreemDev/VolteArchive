package volte.commands.parsers.abs

import com.jagrosh.jdautilities.command.CommandEvent

abstract class VolteArgumentParser<T> {


    /**
     * Parse the value param into a value of T.
     *
     *
     * @param value The value to parse.
     * @param event The command event.
     * @return The parsed value.
     */
    abstract fun parse(event: CommandEvent, value: String): T
}