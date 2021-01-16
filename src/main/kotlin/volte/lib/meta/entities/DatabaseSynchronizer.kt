package volte.lib.meta.entities

import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.Volte
import volte.database.entities.GuildData
import volte.database.entities.SelfRoleRepository
import volte.lib.meta.getData
import volte.lib.meta.getSelfRoles
import java.sql.ResultSet

class DatabaseSynchronizer : ListenerAdapter() {

    override fun onGuildJoin(event: GuildJoinEvent) {
        with(event.guild.getData()) {
            query(selectAll(GuildData.ID.sqlEquals(event.guild.id))) { rs ->
                if (!rs.next()) {
                    modify(
                        insert(
                            hashMapOf(
                                GuildData.ID to event.guild.id,
                                GuildData.OPERATOR to GuildData.OPERATOR.default(),
                                GuildData.PREFIX to GuildData.PREFIX.default(),
                                GuildData.AUTOROLE to GuildData.AUTOROLE.default(),
                                GuildData.MASSPINGS to GuildData.MASSPINGS.default(),
                                GuildData.ANTILINK to GuildData.ANTILINK.default(),
                                GuildData.AUTOQUOTE to GuildData.AUTOQUOTE.default()
                            )
                        )
                    )
                }
            }
        }

        event.guild.loadMembers().onSuccess {
            Volte.logger(this::class) {
                info("Loaded guild ${event.guild.name}'s ${it.size} size member list into the cache.")
            }
        }
    }

    override fun onRoleDelete(event: RoleDeleteEvent) {
        if (event.guild.getSelfRoles().getRoles().contains(event.role.id)) {
            Volte.db().connection().prepareStatement(buildString {
                with(event.guild.getSelfRoles()) {
                    modify(
                        delete(
                            and(
                                SelfRoleRepository.ROLEID.sqlEquals(event.role.id),
                                SelfRoleRepository.GUILDID.sqlEquals(event.guild.id)
                            )
                        )
                    )
                }
            }).executeUpdate()
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