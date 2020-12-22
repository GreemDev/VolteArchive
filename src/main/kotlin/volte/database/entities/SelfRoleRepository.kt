package volte.database.entities

import volte.database.api.*
import volte.database.api.columns.*
import volte.Volte
import volte.database.VolteDatabase
import volte.meta.updateValueOf
import volte.meta.valueOf
import java.sql.ResultSet

data class SelfRoleRepository(val guildId: String): DataManager(Volte.db().connector(), "SELFROLES") {

    val roleIds: ArrayList<String> = arrayListOf()

    init {
        read(selectAll(GUILDID.equalsValue(guildId))) { rs ->
            while (rs.next()) {
                roleIds.add(ROLEID.getValue(rs))
            }
        }
    }

    fun createSelfRole(roleId: String) {
        readWrite(selectAll(GUILDID.equalsValue(guildId))) { rs ->
            val ids = arrayListOf<String>().apply {
                while (rs.next()) {
                    add(rs.valueOf(ROLEID))
                }
            }

            if (!ids.contains(roleId)) {
                rs.moveToInsertRow()
                rs.updateValueOf(GUILDID, guildId)
                rs.updateValueOf(ROLEID, roleId)
                rs.insertRow()
            }
        }
    }

    fun deleteSelfRole(roleId: String) {
        readWrite(selectAll(GUILDID.equalsValue(guildId))) { rs ->
            while (rs.next()) {
                if (rs.valueOf(ROLEID) == roleId) {
                    rs.deleteRow()
                    return@readWrite
                }
            }
        }
    }

    companion object {
        val GUILDID: SQLColumn<String> = StringColumn("GUILDID", false, maxLength = 20)
        val ROLEID: SQLColumn<String> = StringColumn("ROLEID", false, maxLength = 20)
    }

}