package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.BooleanColumn
import com.jagrosh.easysql.columns.StringColumn
import com.jagrosh.jdautilities.command.GuildSettingsProvider
import volte.Volte
import volte.database.VolteDatabase
import volte.meta.equalsValue

class GuildData(private val guildId: String, db: VolteDatabase) : DataManager(db.connector(), "GUILDS"), GuildSettingsProvider {

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
                AUTOQUOTE.updateValue(rs, enabled)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                AUTOQUOTE.updateValue(rs, enabled)
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
                OPERATOR.updateValue(rs, id)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                OPERATOR.updateValue(rs, id)
                rs.insertRow()
            }
        }
    }

    fun getOperator(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, OPERATOR)) { rs ->
            if (rs.next()) OPERATOR.getValue(rs) else ""
        }
    }

    fun setAntilink(enabled: Boolean) {
        readWrite(select(ID.equalsValue(guildId), ID, ANTILINK)) { rs ->
            if (rs.next()) {
                ANTILINK.updateValue(rs, enabled)
                rs.updateRow()
            } else {
                ID.updateValue(rs, guildId)
                ANTILINK.updateValue(rs, enabled)
                rs.insertRow()
            }
        }
    }


    fun getAntilink(): Boolean {
        return read<Boolean>(select(ID.equalsValue(guildId), ID, ANTILINK)) {rs ->
            if (rs.next()) ANTILINK.getValue(rs) else false
        }
    }


    fun getMassPings(): Boolean {
        return read<Boolean>(select(ID.equalsValue(guildId), ID, MASSPINGS)) { rs ->
            if (rs.next()) MASSPINGS.getValue(rs) else false
        }
    }


    fun setMassPings(enabled: Boolean) {
        readWrite(select(ID.equalsValue(guildId), ID, MASSPINGS)) { rs ->
            if (rs.next()) {
                MASSPINGS.updateValue(rs, enabled)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                MASSPINGS.updateValue(rs, enabled)
                rs.insertRow()
            }
        }
    }

    fun setPrefix(prefix: String) {
        readWrite(select(ID.equalsValue(guildId), ID, PREFIX)) { rs ->
            if (rs.next()) {
                PREFIX.updateValue(rs, prefix)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                PREFIX.updateValue(rs, prefix)
                rs.insertRow()
            }
        }
    }

    fun getPrefix(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, PREFIX)) { rs ->
            if (rs.next()) PREFIX.getValue(rs) else Volte.config().prefix()
        }
    }

    override fun getPrefixes(): MutableCollection<String> {
        return mutableListOf(getPrefix())
    }

    fun getAutorole(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, AUTOROLE)) { rs ->
            if (rs.next()) AUTOROLE.getValue(rs) else ""
        }
    }

    fun setAutorole(autorole: String){
        readWrite(select(ID.equalsValue(guildId), ID, AUTOROLE)) { rs ->
            if (rs.next()) {
                AUTOROLE.updateValue(rs, autorole)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                AUTOROLE.updateValue(rs, autorole)
                rs.insertRow()
            }
        }
    }


    fun id(): String = guildId

}

