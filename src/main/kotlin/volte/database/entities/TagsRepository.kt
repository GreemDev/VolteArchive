package volte.database.entities

import volte.Volte
import volte.lib.db.DataManager
import volte.lib.db.SQLColumn
import volte.lib.db.columns.IntegerColumn
import volte.lib.db.columns.StringColumn
import volte.meta.updateValueOf
import volte.meta.valueOf
import java.sql.ResultSet

data class TagsRepository(val guildId: String): DataManager(Volte.db().connector(), "TAGS") {

    fun getTags(): Set<VolteTag> {
        val tags: HashSet<VolteTag> = hashSetOf()
        read(selectAll(VolteTag.GUILD.equalsValue(guildId))) { rs ->
            while (rs.next()) {
                tags.add(VolteTag(rs))
            }
        }
        return tags
    }

    fun deleteTag(name: String) {
        readWrite(select(VolteTag.GUILD.equalsValue(guildId), VolteTag.NAME)) { rs ->
            while (rs.next()) {
                if (rs.valueOf(VolteTag.NAME).equals(name, true)) {
                    rs.deleteRow()
                }
            }
        }
    }

    fun hasTag(name: String): Boolean {
        return read<Boolean>(select(VolteTag.GUILD.equalsValue(guildId), VolteTag.NAME)) { rs ->
            arrayListOf<String>().apply {
                while (rs.next()) {
                    add(rs.valueOf(VolteTag.NAME))
                }
            }.contains(name)
        }
    }

    fun createNewTag(name: String, content: String, creator: String) {
        readWrite(selectAll(VolteTag.GUILD.equalsValue(guildId))) { rs ->
            rs.moveToInsertRow()
            rs.updateValueOf(VolteTag.GUILD, guildId)
            rs.updateValueOf(VolteTag.NAME, name)
            rs.updateValueOf(VolteTag.CONTENT, content)
            rs.updateValueOf(VolteTag.USES, 0)
            rs.updateValueOf(VolteTag.CREATOR, creator)
            rs.insertRow()
        }
    }

    data class VolteTag(private val rs: ResultSet) {

        companion object {
            val GUILD: SQLColumn<String> = StringColumn("GUILDID", false, maxLength = 20)
            val NAME: SQLColumn<String> = StringColumn("NAME", false, maxLength = 20)
            val CONTENT: SQLColumn<String> = StringColumn("CONTENT", false, maxLength = 1950)
            val USES: SQLColumn<Int> = IntegerColumn("USES", false)
            val CREATOR: SQLColumn<String> = StringColumn("CREATOR", false, maxLength = 20)
        }

        fun rs() = rs

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