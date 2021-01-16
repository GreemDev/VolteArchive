package volte.database.entities

import volte.Volte
import volte.lib.db.DbManager
import volte.lib.db.DbColumn
import volte.lib.db.columns.IntegerColumn
import volte.lib.db.columns.StringColumn
import volte.lib.meta.*

class BlacklistRepository(private val guildId: String) : DbManager(Volte.db().connection(), "BLACKLIST") {

    constructor() : this("")

    override fun allColumns(): List<DbColumn<*>> {
        return arrayListOf(ID, GUILDID, PHRASE)
    }

    fun getPhrases() =
        query<ArrayList<String>>(select(GUILDID.sqlEquals(guildId), PHRASE)) { rs ->
            arrayListOf<String>().apply {
                whileNext(rs) {
                    add(valueOf(PHRASE))
                }
            }

        }

    fun createEntry(phrase: String) {
        modify(
            insert(
                hashMapOf(
                    GUILDID to guildId,
                    PHRASE to phrase
                )
            )
        )
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
        modify(delete(PHRASE.sqlEquals(phrase)))
    }


    companion object {
        val ID: DbColumn<Int> = IntegerColumn("ID", false, 0, autoIncrement = true, primaryKey = true)
        val GUILDID: DbColumn<String> = StringColumn("GUILDID", false, "")
        val PHRASE: DbColumn<String> = StringColumn("PHRASE", false, "")
    }
}