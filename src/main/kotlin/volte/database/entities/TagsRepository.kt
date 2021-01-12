package volte.database.entities

import volte.Volte
import volte.lib.db.DataManager
import volte.lib.db.SQLColumn
import volte.lib.db.columns.IntegerColumn
import volte.lib.db.columns.StringColumn
import volte.meta.updateValueOf
import volte.meta.valueOf
import java.sql.ResultSet

data class TagsRepository(val guildId: String) : DataManager(Volte.db().connector(), "TAGS") {
    fun getTags(): ArrayList<VolteTag> = arrayListOf<VolteTag>().apply {
        query(selectAll(VolteTag.GUILD.sqlEquals(guildId))) { rs ->
            whileNext(rs) {
                add(VolteTag(this))
            }
        }
    }


    fun deleteTag(name: String) =
        queryMutable(select(VolteTag.GUILD.sqlEquals(guildId), VolteTag.NAME)) { rs ->
            whileNext(rs) {
                if (valueOf(VolteTag.NAME).equals(name, true)) {
                    deleteRow()
                    return@whileNext
                }
            }
        }


    fun hasTag(name: String): Boolean =
        query<Boolean>(select(VolteTag.GUILD.sqlEquals(guildId), VolteTag.NAME)) { rs ->
            arrayListOf<String>().apply {
                whileNext(rs) {
                    add(valueOf(VolteTag.NAME))
                }
            }.contains(name)
        }


    fun createNewTag(name: String, content: String, creator: String) =
        queryMutable(selectAll(VolteTag.GUILD.sqlEquals(guildId))) { rs ->
            rs.moveToInsertRow()
            rs.updateValueOf(VolteTag.GUILD, guildId)
            rs.updateValueOf(VolteTag.NAME, name)
            rs.updateValueOf(VolteTag.CONTENT, content)
            rs.updateValueOf(VolteTag.USES, 0)
            rs.updateValueOf(VolteTag.CREATOR, creator)
            rs.insertRow()
        }


    data class VolteTag(private val rs: ResultSet) {

        companion object {
            val GUILD: SQLColumn<String> = StringColumn("GUILDID", false, maxLength = 20)
            val NAME: SQLColumn<String> = StringColumn("NAME", false, maxLength = 20)
            val CONTENT: SQLColumn<String> = StringColumn("CONTENT", false, maxLength = 1950)
            val USES: SQLColumn<Int> = IntegerColumn("USES", false)
            val CREATOR: SQLColumn<String> = StringColumn("CREATOR", false, maxLength = 20)
        }

        private val name: String = rs.valueOf(NAME)
        private val content: String = rs.valueOf(CONTENT)
        private val uses: Int = rs.valueOf(USES)
        private val creatorId: String = rs.valueOf(CREATOR)

        fun name() = name
        fun content() = content
        fun uses() = uses
        fun creatorId() = creatorId
    }

}