package volte.commands.parsers

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import volte.commands.parsers.abstractions.VolteArgumentParser
import volte.static
import java.awt.Color

object Parsers {
    @static fun member(): VolteArgumentParser<Member?> = MemberParser()
    @static fun role(): VolteArgumentParser<Role?> = RoleParser()
    @static fun boolean(): VolteArgumentParser<Boolean?> = BooleanParser()
    @static fun color(): VolteArgumentParser<Color?> = ColorParser()
    @static fun channel(): VolteArgumentParser<TextChannel?> = TextChannelParser()
}