package volte.commands.parsers

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import volte.commands.parsers.abs.VolteArgumentParser
import volte.meta.static
import java.awt.Color

/**
 * Singleton instances of [VolteArgumentParser]
 */
object Parsers {
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