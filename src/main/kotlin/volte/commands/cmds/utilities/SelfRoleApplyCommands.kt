package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.commands.parsers.Parsers
import volte.meta.*
import volte.meta.categories.utility

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
                setTitle("You didn't provide a valid role.")
                setDescription("Try using an ID or an @ next time.")
            }
        } hasValue { role ->
            if (selfRoles.roleIds.isEmpty()) {
                event.replyInline {
                    setTitle("This guild does not have any roles available to self-assign!")
                }
                return@hasValue
            }

            selfRoles.roleIds.firstOrNull(role.id::equals).optional() hasValue {
                event.guild.addRoleToMember(event.member, role) then {
                    event.replyInline {
                        setTitle("Success!")
                        setDescription("Gave you the ${role.asMention} role.")
                    }
                }
            } hasNoValue {
                event.replyInline {
                    setTitle("This guild does not have ${role.asMention} as a role to self-assign.")
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
                setTitle("You didn't provide a valid role.")
                setDescription("Try using an ID or an @ next time.")
            }
        } hasValue { role ->
            if (selfRoles.roleIds.isEmpty()) {
                event.replyInline {
                    setTitle("This guild does not have any roles available to self-assign!")
                }
                return@hasValue
            }

            selfRoles.roleIds.firstOrNull(role.id::equals).optional() hasValue {
                event.guild.removeRoleFromMember(event.member, role) then {
                    event.replyInline {
                        setTitle("Success!")
                        setDescription("Took away your ${role.asMention} role.")
                    }
                }
            } hasNoValue {
                event.replyInline {
                    setTitle("This guild does not have ${role.asMention} as a role to self-assign.")
                }
            }
        }
    }

}