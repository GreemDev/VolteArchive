package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.StringColumn
import volte.Volte
import volte.meta.*

class BlacklistRepository(private val guildId: String): DataManager(Volte.db().connector(), "BLACKLIST") {

    private val entries: ArrayList<String> = arrayListOf()

    init {
        read(select(GUILDID.equalsValue(guildId), PHRASE)) { rs ->
            while (rs.next()) {
                entries.add(rs.valueOf(PHRASE))
            }
        }
    }

    fun createEntry(phrase: String) {
        readWrite(select(GUILDID.equalsValue(guildId))) { rs ->
            rs.moveToInsertRow()
            rs.updateValueOf(GUILDID, guildId)
            rs.updateValueOf(PHRASE, phrase)
            rs.insertRow()
        }
    }

    fun removeEntry(phrase: String) {
        readWrite(selectAll(GUILDID.equalsValue(guildId))) { rs ->
            while (rs.next()) {
                if (rs.valueOf(PHRASE).equals(phrase, true)) {
                    rs.deleteRow()
                    return@readWrite
                }
            }
        }
    }


    companion object {
        val GUILDID: SQLColumn<String> = StringColumn("GUILDID", false, "", 20)
        val PHRASE: SQLColumn<String> = StringColumn("PHRASE", false, "", 200)
    }
}