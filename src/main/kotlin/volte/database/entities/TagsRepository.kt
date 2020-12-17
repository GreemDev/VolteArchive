package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.IntegerColumn
import com.jagrosh.easysql.columns.StringColumn
import volte.database.VolteDatabase
import volte.meta.equalsValue
import java.sql.ResultSet

class TagsRepository(db: VolteDatabase, guildId: String): DataManager(db.connector(), "TAGS") {

    private val tags: HashSet<VolteTag> = hashSetOf()
    fun tags(): HashSet<VolteTag> = tags

    init {
        read(selectAll(VolteTag.GUILD.equalsValue(guildId))) { rs ->
            while (rs.next()) {
                tags.add(VolteTag(rs))
            }
        }

    }

    data class VolteTag(private val rs: ResultSet) {

        companion object {
            val GUILD: SQLColumn<String> = StringColumn("GUILDID", false, "", 20)
            val NAME: SQLColumn<String> = StringColumn("NAME", false, "", 20)
            val CONTENT: SQLColumn<String> = StringColumn("CONTENT", false, "", 1950)
            val USES: SQLColumn<Int> = IntegerColumn("USES", false, 1)
            val CREATOR: SQLColumn<String> = StringColumn("CREATOR", false, "", 20)
        }


        private val name: String = rs.getString("name")
        private val content: String = rs.getString("content")
        private val uses: Int = rs.getInt("uses")
        private val creatorId: String = rs.getString("creator")

        fun name() = name
        fun content() = content
        fun uses() = uses
        fun creatorId() = creatorId
    }

}