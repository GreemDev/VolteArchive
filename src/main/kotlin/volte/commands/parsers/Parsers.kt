package volte.commands.parsers

import com.jagrosh.jdautilities.command.CommandEvent
import com.sun.jdi.InvalidTypeException
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import volte.commands.parsers.abs.VolteArgumentParser
import volte.meta.static
import java.awt.Color

/**
 * Singleton instances of [VolteArgumentParser] and utilities.
 */
object Parsers {

    inline fun <reified T> parse(event: CommandEvent, value: String): T? {
        return when (T::class) {
            Member::class -> {
                member().parse(event, value) as T?
            }
            Role::class -> {
                role().parse(event, value) as T?
            }
            Boolean::class -> {
                boolean().parse(event, value) as T?
            }
            Color::class -> {
                color().parse(event, value) as T?
            }
            TextChannel::class -> {
                channel().parse(event,value) as T?
            }
            else -> {
                throw InvalidTypeException("${T::class.java.simpleName} doesn't have a known VolteArgumentParser!")
            }
        }
    }

    // Instances
    private val member = MemberParser()
    private val role = RoleParser()
    private val boolean = BooleanParser()
    private val color = ColorParser()
    private val channel = TextChannelParser()

    //getters
    @static
    fun member(): VolteArgumentParser<Member?> = member
    @static
    fun role(): VolteArgumentParser<Role?> = role
    @static
    fun boolean(): VolteArgumentParser<Boolean?> = boolean
    @static
    fun color(): VolteArgumentParser<Color?> = color
    @static
    fun channel(): VolteArgumentParser<TextChannel?> = channel
}