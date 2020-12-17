package volte.commands.parsers

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import volte.commands.parsers.abstractions.VolteArgumentParser
import volte.static

object Parsers {

    @static fun member(): VolteArgumentParser<Member?> = MemberParser()
    @static fun role(): VolteArgumentParser<Role?> = RoleParser()
    @static fun boolean(): VolteArgumentParser<Boolean?> = BooleanParser()

}