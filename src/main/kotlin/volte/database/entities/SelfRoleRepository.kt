package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.StringColumn
import volte.Volte
import volte.database.VolteDatabase
import volte.meta.equalsValue
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
            val ids = arrayListOf<String>()
            while (rs.next()) {
                ids.add(rs.valueOf(ROLEID))
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
                }
            }
        }
    }

    companion object {
        val GUILDID: SQLColumn<String> = StringColumn("GUILDID", false, "", 20)
        val ROLEID: SQLColumn<String> = StringColumn("ROLEID", false, "", 20)
    }

}