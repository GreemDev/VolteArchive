package volte.database.entities

import volte.Volte
import volte.lib.db.DataManager
import volte.lib.db.SQLColumn
import volte.lib.db.columns.StringColumn
import volte.meta.updateValueOf
import volte.meta.valueOf

data class SelfRoleRepository(val guildId: String): DataManager(Volte.db().connector(), "SELFROLES") {

    val roleIds: ArrayList<String> = read<ArrayList<String>>(select(GUILDID.equalsValue(guildId), ROLEID)) { rs ->
        arrayListOf<String>().apply {
            while (rs.next()) {
                add(rs.valueOf(ROLEID))
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

    fun hasSelfRole(roleId: String): Boolean {
        return read<Boolean>(select(GUILDID.equalsValue(guildId), ROLEID)) { rs ->
            arrayListOf<String>().apply {
                while (rs.next()) {
                    add(rs.valueOf(ROLEID))
                }
            }.contains(roleId)
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