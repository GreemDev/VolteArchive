package volte.database.entities

import volte.Volte
import volte.lib.db.DataManager
import volte.lib.db.SQLColumn
import volte.lib.db.columns.StringColumn
import volte.meta.*

class BlacklistRepository(private val guildId: String) : DataManager(Volte.db().connector(), "BLACKLIST") {

    val blacklistedPhrases
        get() =
            query<ArrayList<String>>(select(GUILDID.sqlEquals(guildId), PHRASE)) { rs ->
                arrayListOf<String>().apply {
                    whileNext(rs) {
                        add(valueOf(PHRASE))
                    }
                }

            }

    fun createEntry(phrase: String) {
        queryMutable(select(GUILDID.sqlEquals(guildId))) { rs ->
            rs.moveToInsertRow()
            rs.updateValueOf(GUILDID, guildId)
            rs.updateValueOf(PHRASE, phrase)
            rs.insertRow()
        }
    }

    fun hasEntry(phrase: String): Boolean {
        return query<Boolean>(select(GUILDID.sqlEquals(guildId), PHRASE)) { rs ->
            arrayListOf<String>().apply {
                whileNext(rs) {
                    add(valueOf(PHRASE))
                }
            }.contains(phrase)
        }
    }

    fun removeEntry(phrase: String) {
        queryMutable(selectAll(GUILDID.sqlEquals(guildId))) { rs ->
            whileNext(rs) {
                if (rs.valueOf(PHRASE).equals(phrase, true)) {
                    rs.deleteRow()
                    return@whileNext
                }
            }
        }
    }


    companion object {
        val GUILDID: SQLColumn<String> = StringColumn("GUILDID", false, "")
        val PHRASE: SQLColumn<String> = StringColumn("PHRASE", false, "")
    }
}