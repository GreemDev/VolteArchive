package volte.meta.entities

import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte
import volte.database.entities.SelfRoleRepository
import volte.meta.getData
import volte.meta.getSelfRoles
import java.sql.ResultSet

class DatabaseSynchronizer : ListenerAdapter() {

    override fun onGuildJoin(event: GuildJoinEvent) {
        val statement = Volte.db().connector().connection().createStatement()
        val rs = statement.executeQuery("SELECT * FROM GUILDS WHERE ID = ${event.guild.id}").takeIf(ResultSet::next)
        if (rs == null) {
            statement.executeUpdate(
                "INSERT INTO GUILDS VALUES('${event.guild.id}', '', '', '${
                    Volte.config().prefix()
                }', FALSE, FALSE, FALSE)"
            )
        }
        event.guild.loadMembers().onSuccess {
            Volte.logger(this::class) {
                info("Loaded guild ${event.guild.name}'s ${it.size} size member list into the cache.")
            }
        }
    }

    override fun onRoleDelete(event: RoleDeleteEvent) {
        if (event.guild.getSelfRoles().roleIds.contains(event.role.id)) {
            with(Volte.db().connector().connection().createStatement()) {
                executeUpdate(
                    "DELETE FROM SELFROLES WHERE " +
                            SelfRoleRepository.ROLEID.sqlEquals(event.role.id)
                            + " AND " +
                            SelfRoleRepository.GUILDID.sqlEquals(event.guild.id)
                )
                closeOnCompletion()
            }
            return
        }

        with(event.guild.getData()) {
            if (getAutorole() == event.role.id) {
                setAutorole("")
                return
            }

            if (getOperator() == event.role.id) {
                setOperator("")
                return
            }
        }
    }


}