package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.BooleanColumn
import com.jagrosh.easysql.columns.StringColumn
import com.jagrosh.jdautilities.command.GuildSettingsProvider
import org.h2.jdbc.JdbcResultSet
import volte.Volte
import volte.database.VolteDatabase
import volte.meta.equalsValue
import volte.meta.updateValueOf
import volte.meta.valueOf

data class GuildData(private val guildId: String) : DataManager(Volte.db().connector(), "GUILDS"), GuildSettingsProvider {

    companion object {
        val ID: SQLColumn<String> = StringColumn("ID", false, "", 20)
        val OPERATOR: SQLColumn<String> = StringColumn("OPERATOR", false, "", 20)
        val PREFIX: SQLColumn<String> = StringColumn("PREFIX", false, Volte.config().prefix(), 20)
        val AUTOROLE: SQLColumn<String> = StringColumn("AUTOROLE", false, "", 20)
        val MASSPINGS: SQLColumn<Boolean> = BooleanColumn("MASSPINGS", false, false)
        val ANTILINK: SQLColumn<Boolean> = BooleanColumn("ANTILINK", false, false)
        val AUTOQUOTE: SQLColumn<Boolean> = BooleanColumn("AUTOQUOTE", false, false)
    }

    fun setAutoQuote(enabled: Boolean) {
        readWrite(select(ID.equalsValue(guildId), ID, AUTOQUOTE)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(AUTOQUOTE, enabled)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(AUTOQUOTE, enabled)
                rs.insertRow()
            }
        }
    }

    fun getAutoQuote(): Boolean {
        return read<Boolean>(select(ID.equalsValue(guildId), ID, AUTOQUOTE)) { rs ->
            if (rs.next()) AUTOQUOTE.getValue(rs) else false
        }
    }


    fun setOperator(id: String) {
        readWrite(select(ID.equalsValue(guildId), ID, OPERATOR)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(OPERATOR, id)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(OPERATOR, id)
                rs.insertRow()
            }
        }
    }

    fun getOperator(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, OPERATOR)) { rs ->
            if (rs.next()) rs.valueOf(OPERATOR) else ""
        }
    }

    fun setAntilink(enabled: Boolean) {
        readWrite(select(ID.equalsValue(guildId), ID, ANTILINK)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(ANTILINK, enabled)
                rs.updateRow()
            } else {
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(ANTILINK, enabled)
                rs.insertRow()
            }
        }
    }


    fun getAntilink(): Boolean {
        return read<Boolean>(select(ID.equalsValue(guildId), ID, ANTILINK)) {rs ->
            if (rs.next()) rs.valueOf(ANTILINK) else false
        }
    }


    fun getMassPings(): Boolean {
        return read<Boolean>(select(ID.equalsValue(guildId), ID, MASSPINGS)) { rs ->
            if (rs.next()) rs.valueOf(MASSPINGS) else false
        }
    }


    fun setMassPings(enabled: Boolean) {
        readWrite(select(ID.equalsValue(guildId), ID, MASSPINGS)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(MASSPINGS, enabled)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(MASSPINGS, enabled)
                rs.insertRow()
            }
        }
    }

    fun setPrefix(prefix: String) {
        readWrite(select(ID.equalsValue(guildId), ID, PREFIX)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(PREFIX, prefix)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(PREFIX, prefix)
                rs.insertRow()
            }
        }
    }

    fun getPrefix(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, PREFIX)) { rs ->
            if (rs.next()) rs.valueOf(PREFIX) else Volte.config().prefix()
        }
    }

    override fun getPrefixes(): MutableCollection<String> {
        return mutableListOf(getPrefix())
    }

    fun getAutorole(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, AUTOROLE)) { rs ->
            if (rs.next()) rs.valueOf(AUTOROLE) else ""
        }
    }

    fun setAutorole(autorole: String){
        readWrite(select(ID.equalsValue(guildId), ID, AUTOROLE)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(AUTOROLE, autorole)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(AUTOROLE, autorole)
                rs.insertRow()
            }
        }
    }
}

