package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.commands.parsers.Parsers
import volte.lib.meta.*
import volte.lib.meta.categories.utility

class IamCommand : Command() {
    init {
        this.name = "iam"
        this.help = "Gives yourself a role from the guild's self-assignable roles list."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        val selfRoles = event.guild.getSelfRoles()
        Parsers.role().parse(event, event.args).optional() hasNoValue {
            event.replyInline {
                title("You didn't provide a valid role.")
                description("Try using an ID or an @ next time.")
            }
        } hasValue { role ->
            val roles = selfRoles.getRoles()
            if (roles.isEmpty()) {
                event.replyInline {
                    title("This guild does not have any roles available to self-assign!")
                }
                return@hasValue
            }

            roles.firstOrNull(role.id::equals).optional() hasValue {
                event.guild.addRoleToMember(event.member, role) then {
                    event.replyInline {
                        title("Success!")
                        description("Gave you the ${role.asMention} role.")
                    }
                }
            } hasNoValue {
                event.replyInline {
                    title("This guild does not have ${role.asMention} as a role to self-assign.")
                }
            }
        }
    }

}

class IamNotCommand : Command() {

    init {
        this.name = "iamnot"
        this.help = "Takes away a role from yourself from the guild's self-assignable roles list."
        this.guildOnly = true
        this.category = utility()
    }

    override fun execute(event: CommandEvent) {
        val selfRoles = event.guild.getSelfRoles()
        Parsers.role().parse(event, event.args).optional() hasNoValue {
            event.replyInline {
                title("You didn't provide a valid role.")
                description("Try using an ID or an @ next time.")
            }
        } hasValue { role ->
            val roles = selfRoles.getRoles()
            if (roles.isEmpty()) {
                event.replyInline {
                    title("This guild does not have any roles available to self-assign!")
                }
                return@hasValue
            }

            roles.firstOrNull(role.id::equals).optional() hasValue {
                event.guild.removeRoleFromMember(event.member, role) then {
                    event.replyInline {
                        title("Success!")
                        description("Took away your ${role.asMention} role.")
                    }
                }
            } hasNoValue {
                event.replyInline {
                    title("This guild does not have ${role.asMention} as a role to self-assign.")
                }
            }
        }
    }
}

class SelfRoleListCommand : Command() {
    override fun execute(event: CommandEvent) {
        val roles = event.guild.getSelfRoles().getRoles().map(event.guild::getRoleById)

        
    }

}