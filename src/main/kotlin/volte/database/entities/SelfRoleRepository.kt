package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.StringColumn
import volte.database.VolteDatabase
import volte.meta.equalsValue
import java.sql.ResultSet

class SelfRoleRepository(db: VolteDatabase, guildId: String): DataManager(db.connector(), "SELFROLES") {

    val roleIds: ArrayList<String> = arrayListOf()

    init {
        read(selectAll(VolteSelfRole.GUILDID.equalsValue(guildId))) { rs ->
            while (rs.next()) {
                roleIds.add(VolteSelfRole.ROLEID.getValue(rs))
            }
        }
    }

    data class VolteSelfRole(private val rs: ResultSet) {

        companion object {
            val GUILDID: SQLColumn<String> = StringColumn("GUILDID", false, "", 20)
            val ROLEID: SQLColumn<String> = StringColumn("ROLEID", false, "", 20)
        }

    }

}