package volte.commands.cmds.utilities

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.commands.parsers.Parsers
import volte.meta.*

class IamCommand : Command() {
    init {
        this.name = "iam"
        this.help = "Gives yourself a role from the guild's self-assignable roles list."
        this.guildOnly = true
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        val selfRoles = event.guild.getSelfRoles()
        val roleOpt = Parsers.role().parse(event, event.args).optional()
        roleOpt.ifNotPresent {
            event.replyInline {
                setTitle("You didn't provide a valid role.")
                setDescription("Try using an ID or an @ next time.")
            }
        }

        roleOpt.ifPresent { role ->
            if (selfRoles.roleIds.isEmpty()) {
                event.replyInline {
                    setTitle("This guild does not have any roles available to self-assign!")
                }
                return@ifPresent
            }
            val selfRole = selfRoles.roleIds.firstOrNull { it == role.id }.optional()
            selfRole.ifNotPresent {
                event.replyInline {
                    setTitle("This guild does not have ${role.asMention} as a role to self-assign.")
                }
            }
            selfRole.ifPresent {
                event.guild.removeRoleFromMember(event.member, role).then {
                    event.replyInline {
                        setTitle("Success!")
                        setDescription("Took away your ${role.asMention} role.")
                    }
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
        this.category = Constants.utilityCategory()
    }

    override fun execute(event: CommandEvent) {
        val selfRoles = event.guild.getSelfRoles()
        val roleOpt = Parsers.role().parse(event, event.args).optional()
        roleOpt.ifNotPresent {
            event.replyInline {
                setTitle("You didn't provide a valid role.")
                setDescription("Try using an ID or an @ next time.")
            }
        }

        roleOpt.ifPresent { role ->
            if (selfRoles.roleIds.isEmpty()) {
                event.replyInline {
                    setTitle("This guild does not have any roles available to self-assign!")
                }
                return@ifPresent
            }
            val selfRole = selfRoles.roleIds.firstOrNull { entry -> role.id == entry}.optional()
            selfRole.ifNotPresent {
                event.replyInline {
                    setTitle("This guild does not have ${role.asMention} as a role to self-assign.")
                }
            }
            selfRole.ifPresent {
                event.guild.addRoleToMember(event.member, role).then {
                    event.replyInline {
                        setTitle("Success!")
                        setDescription("Gave you the ${role.asMention} role.")
                    }
                }
            }
        }
    }

}