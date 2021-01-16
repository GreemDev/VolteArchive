package volte.database.entities

import volte.Volte
import volte.lib.db.DbManager
import volte.lib.db.DbColumn
import volte.lib.db.columns.IntegerColumn
import volte.lib.db.columns.StringColumn
import volte.lib.meta.valueOf
import java.sql.ResultSet

data class TagsRepository(val guildId: String) : DbManager(Volte.db().connection(), "TAGS") {

    constructor() : this("")

    override fun allColumns(): List<DbColumn<*>> {
        return arrayListOf(VolteTag.ID, VolteTag.GUILD, VolteTag.NAME, VolteTag.CONTENT, VolteTag.USES, VolteTag.CREATOR)
    }

    fun getTags(): ArrayList<VolteTag> = arrayListOf<VolteTag>().apply {
        query(selectAll(VolteTag.GUILD.sqlEquals(guildId))) { rs ->
            whileNext(rs) {
                add(VolteTag(this))
            }
        }
    }


    fun deleteTag(name: String) =
        modify(delete(VolteTag.NAME.sqlEquals(name)))


    fun hasTag(name: String): Boolean =
        query<Boolean>(select(VolteTag.GUILD.sqlEquals(guildId), VolteTag.NAME)) { rs ->
            arrayListOf<String>().apply {
                whileNext(rs) {
                    add(valueOf(VolteTag.NAME))
                }
            }.contains(name)
        }


    fun createNewTag(name: String, content: String, creator: String) =
        modify(insert(hashMapOf(
            VolteTag.GUILD to guildId,
            VolteTag.NAME to name,
            VolteTag.CONTENT to content,
            VolteTag.USES to 0,
            VolteTag.CREATOR to creator
        )))


    data class VolteTag(private val rs: ResultSet) {

        companion object {
            val ID: DbColumn<Int> = IntegerColumn("ID", false, 0, autoIncrement = true, primaryKey = true)
            val GUILD: DbColumn<String> = StringColumn("GUILDID", false, maxLength = 20)
            val NAME: DbColumn<String> = StringColumn("NAME", false, maxLength = 20)
            val CONTENT: DbColumn<String> = StringColumn("CONTENT", false, maxLength = 1950)
            val USES: DbColumn<Int> = IntegerColumn("USES", false)
            val CREATOR: DbColumn<String> = StringColumn("CREATOR", false, maxLength = 20)
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