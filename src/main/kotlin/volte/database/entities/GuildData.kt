package volte.database.entities

import com.jagrosh.jdautilities.command.GuildSettingsProvider
import volte.Volte
import volte.lib.db.DataManager
import volte.lib.db.SQLColumn
import volte.lib.db.columns.BooleanColumn
import volte.lib.db.columns.StringColumn
import volte.meta.*

data class GuildData(private val guildId: String) : DataManager(Volte.db().connector(), "GUILDS"),
    GuildSettingsProvider {

    companion object {
        val ID: SQLColumn<String> = StringColumn("ID", false, "", true, 20)
        val OPERATOR: SQLColumn<String> = StringColumn("OPERATOR", false, maxLength = 20)
        val PREFIX: SQLColumn<String> = StringColumn("PREFIX", false, Volte.config().prefix())
        val AUTOROLE: SQLColumn<String> = StringColumn("AUTOROLE", false, maxLength = 20)
        val MASSPINGS: SQLColumn<Boolean> = BooleanColumn("MASSPINGS", false)
        val ANTILINK: SQLColumn<Boolean> = BooleanColumn("ANTILINK", false)
        val AUTOQUOTE: SQLColumn<Boolean> = BooleanColumn("AUTOQUOTE", false)
    }

    override fun allColumns(): List<SQLColumn<*>> {
        return listOf(ID, OPERATOR, PREFIX, AUTOROLE, MASSPINGS, ANTILINK, AUTOQUOTE)
    }

    fun setAutoQuote(enabled: Boolean) {
        queryMutable(select(ID.equalsValue(guildId), ID, AUTOQUOTE)) { rs ->
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
        return query<Boolean>(select(ID.equalsValue(guildId), ID, AUTOQUOTE)) { rs ->
            if (rs.next()) rs.valueOf(AUTOQUOTE) else AUTOQUOTE.default()
        }
    }


    fun setOperator(id: String) {
        queryMutable(select(ID.equalsValue(guildId), ID, OPERATOR)) { rs ->
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
        return query<String>(select(ID.equalsValue(guildId), ID, OPERATOR)) { rs ->
            if (rs.next()) rs.valueOf(OPERATOR) else OPERATOR.default()
        }
    }

    fun setAntilink(enabled: Boolean) {
        queryMutable(select(ID.equalsValue(guildId), ID, ANTILINK)) { rs ->
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
        return query<Boolean>(select(ID.equalsValue(guildId), ID, ANTILINK)) { rs ->
            if (rs.next()) rs.valueOf(ANTILINK) else ANTILINK.default()
        }
    }


    fun getMassPings(): Boolean {
        return query<Boolean>(select(ID.equalsValue(guildId), ID, MASSPINGS)) { rs ->
            if (rs.next()) rs.valueOf(MASSPINGS) else MASSPINGS.default()
        }
    }


    fun setMassPings(enabled: Boolean) {
        queryMutable(select(ID.equalsValue(guildId), ID, MASSPINGS)) { rs ->
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
        queryMutable(select(ID.equalsValue(guildId), ID, PREFIX)) { rs ->
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
        return query<String>(select(ID.equalsValue(guildId), ID, PREFIX)) { rs ->
            if (rs.next()) rs.valueOf(PREFIX) else Volte.config().prefix()
        }
    }

    override fun getPrefixes(): MutableCollection<String> {
        return mutableListOf(getPrefix())
    }

    fun getAutorole(): String {
        return query<String>(select(ID.equalsValue(guildId), ID, AUTOROLE)) { rs ->
            return@query if (rs.next()) rs.valueOf(AUTOROLE) else AUTOROLE.default()
        }
    }

    fun setAutorole(autorole: String) {
        queryMutable(select(ID.equalsValue(guildId), ID, AUTOROLE)) { rs ->
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

