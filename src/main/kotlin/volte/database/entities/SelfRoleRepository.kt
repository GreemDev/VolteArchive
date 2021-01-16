package volte.database.entities

import volte.Volte
import volte.lib.db.DbManager
import volte.lib.db.DbColumn
import volte.lib.db.columns.IntegerColumn
import volte.lib.db.columns.StringColumn
import volte.lib.meta.valueOf

data class SelfRoleRepository(val guildId: String) : DbManager(Volte.db().connection(), "SELFROLES") {

    constructor() : this("")

    override fun allColumns(): List<DbColumn<*>> {
        return arrayListOf(ID, GUILDID, ROLEID)
    }

    fun getRoles(): ArrayList<String> = query<ArrayList<String>>(select(GUILDID.sqlEquals(guildId), ROLEID)) { rs ->
        arrayListOf<String>().apply {
            whileNext(rs) {
                add(valueOf(ROLEID))
            }
        }
    }

    fun createSelfRole(roleId: String) {
        modify(insert(hashMapOf(
            GUILDID to guildId,
            ROLEID to roleId
        )))
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
        modify(delete(ROLEID.sqlEquals(roleId)))
    }

    companion object {
        val ID: DbColumn<Int> = IntegerColumn("ID", false, 0, autoIncrement = true, primaryKey = true)
        val GUILDID: DbColumn<String> = StringColumn("GUILDID", false, maxLength = 20)
        val ROLEID: DbColumn<String> = StringColumn("ROLEID", false, maxLength = 20)
    }

}