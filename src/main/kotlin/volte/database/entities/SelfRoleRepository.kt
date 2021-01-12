package volte.database.entities

import volte.Volte
import volte.lib.db.DataManager
import volte.lib.db.SQLColumn
import volte.lib.db.columns.StringColumn
import volte.meta.updateValueOf
import volte.meta.valueOf

data class SelfRoleRepository(val guildId: String) : DataManager(Volte.db().connector(), "SELFROLES") {

    val roleIds: ArrayList<String> = query<ArrayList<String>>(select(GUILDID.sqlEquals(guildId), ROLEID)) { rs ->
        arrayListOf<String>().apply {
            whileNext(rs) {
                add(valueOf(ROLEID))
            }
        }
    }

    fun createSelfRole(roleId: String) {
        queryMutable(selectAll(GUILDID.sqlEquals(guildId))) { rs ->
            val ids = arrayListOf<String>().apply {
                whileNext(rs) {
                    add(valueOf(ROLEID))
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
        return query<Boolean>(select(GUILDID.sqlEquals(guildId), ROLEID)) { rs ->
            arrayListOf<String>().apply {
                whileNext(rs) {
                    add(valueOf(ROLEID))
                }
            }.contains(roleId)
        }
    }

    fun deleteSelfRole(roleId: String) {
        queryMutable(selectAll(GUILDID.sqlEquals(guildId))) { rs ->
            whileNext(rs) {
                if (valueOf(ROLEID) == roleId) {
                    deleteRow()
                    return@whileNext
                }
            }
        }
    }

    companion object {
        val GUILDID: SQLColumn<String> = StringColumn("GUILDID", false, maxLength = 20)
        val ROLEID: SQLColumn<String> = StringColumn("ROLEID", false, maxLength = 20)
    }

}