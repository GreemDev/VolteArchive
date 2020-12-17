package volte.entities

import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte

class DatabaseSynchronizer : ListenerAdapter() {

    override fun onGuildJoin(event: GuildJoinEvent) {
        val statement = Volte.db().connector().connection.createStatement()
        val rs = statement.executeQuery("SELECT * FROM GUILDS WHERE ID = ${event.guild.id}")
        if (rs.next().not()) {
            statement.executeUpdate("INSERT INTO GUILDS VALUES('${event.guild.id}', '', '', '${Volte.config().prefix()}', FALSE, FALSE)")
        }
    }

    override fun onRoleDelete(event: RoleDeleteEvent) {
        val statement = Volte.db().connector().connection.createStatement()
        val settings = Volte.db().getSettingsFor(event.guild.id)
        val selfRoles = Volte.db().getSelfRolesFor(event.guild.id)

        if (settings.getAutorole() == event.role.id) {
            statement.executeUpdate("UPDATE GUILDS SET AUTOROLE = '' WHERE ID = '${event.guild.id}'")
            return
        }

        if (settings.getOperator() == event.role.id) {
            statement.executeUpdate("UPDATE GUILDS SET OPERATOR = '' WHERE ID = '${event.guild.id}'")
            return
        }

        if (selfRoles.roleIds.contains(event.role.id)) {
            statement.executeUpdate("DELETE FROM SELFROLES WHERE ROLEID = '${event.role.id}' AND GUILDID = '${event.guild.id}'")
        }
    }


}