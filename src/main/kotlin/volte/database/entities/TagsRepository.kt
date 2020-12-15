package volte.database.entities

import com.jagrosh.easysql.DataManager
import volte.database.VolteDatabase
import volte.meta.equalsValue

class TagsRepository(db: VolteDatabase, guildId: String): DataManager(db.connector(), "tags") {

    private val tags: HashSet<VolteTag> = hashSetOf()
    fun tags(): HashSet<VolteTag> = tags

    init {
        read(selectAll(VolteTag.GUILD.equalsValue(guildId))) { rs ->
            while (rs.next()) {
                tags.add(VolteTag(rs))
            }
        }

    }
}