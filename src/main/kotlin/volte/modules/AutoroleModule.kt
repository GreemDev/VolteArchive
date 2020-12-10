package volte.modules

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.database.VolteDatabase

class AutoroleModule: ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val roleId = getDatabaseInfo(event) ?: return
        val role = event.guild.getRoleById(roleId)
        event.guild.addRoleToMember(event.member, role ?: return).queue()
    }

    private fun getDatabaseInfo(event: GuildMemberJoinEvent): String? {
        val db = VolteDatabase.createNew()
        val toReturn = db.getStringFor(event.guild.id, "autorole")
        db.closeConnection()
        return if (toReturn == "") null else toReturn
    }

}