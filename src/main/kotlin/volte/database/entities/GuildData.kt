package volte.database.entities

import com.jagrosh.jdautilities.command.GuildSettingsProvider
import volte.Volte
import volte.lib.db.DbManager
import volte.lib.db.DbColumn
import volte.lib.db.columns.BooleanColumn
import volte.lib.db.columns.StringColumn
import volte.lib.meta.*

data class GuildData(private val guildId: String) : DbManager(Volte.db().connection(), "GUILDS"),
    GuildSettingsProvider {

    constructor() : this("")

    companion object {
        val ID: DbColumn<String> = StringColumn("ID", false, "", true, 20)
        val OPERATOR: DbColumn<String> = StringColumn("OPERATOR", false, maxLength = 20)
        val PREFIX: DbColumn<String> = StringColumn("PREFIX", false, Volte.config().prefix())
        val AUTOROLE: DbColumn<String> = StringColumn("AUTOROLE", false, maxLength = 20)
        val MASSPINGS: DbColumn<Boolean> = BooleanColumn("MASSPINGS", false)
        val ANTILINK: DbColumn<Boolean> = BooleanColumn("ANTILINK", false)
        val AUTOQUOTE: DbColumn<Boolean> = BooleanColumn("AUTOQUOTE", false)
    }

    override fun allColumns(): List<DbColumn<*>> {
        return listOf(ID, OPERATOR, PREFIX, AUTOROLE, MASSPINGS, ANTILINK, AUTOQUOTE)
    }

    fun setAutorole(autorole: String) {
        modify(
            update(
                ID.sqlEquals(guildId), hashMapOf(
                    AUTOROLE to autorole
                )
            )
        )
    }

    fun setMassPings(enabled: Boolean) {
        modify(
            update(
                ID.sqlEquals(guildId), hashMapOf(
                    MASSPINGS to enabled
                )
            )
        )
    }

    fun setPrefix(prefix: String) {
        modify(
            update(
                ID.sqlEquals(guildId), hashMapOf(
                    PREFIX to prefix
                )
            )
        )
    }

    fun setAutoQuote(enabled: Boolean) {
        modify(
            update(
                ID.sqlEquals(guildId), hashMapOf(
                    AUTOQUOTE to enabled
                )
            )
        )
    }


    fun setOperator(id: String) {
        modify(
            update(
                ID.sqlEquals(guildId), hashMapOf(
                    OPERATOR to id
                )
            )
        )
    }


    fun setAntilink(enabled: Boolean) {
        modify(
            update(
                ID.sqlEquals(guildId), hashMapOf(
                    ANTILINK to enabled
                )
            )
        )
    }


    fun getAntilink(): Boolean {
        return query<Boolean>(select(ID.sqlEquals(guildId), ID, ANTILINK)) { rs ->
            if (rs.next()) rs.valueOf(ANTILINK) else ANTILINK.default()
        }
    }


    fun getMassPings(): Boolean {
        return query<Boolean>(select(ID.sqlEquals(guildId), ID, MASSPINGS)) { rs ->
            if (rs.next()) rs.valueOf(MASSPINGS) else MASSPINGS.default()
        }
    }

    fun getOperator(): String {
        return query<String>(select(ID.sqlEquals(guildId), ID, OPERATOR)) { rs ->
            if (rs.next()) rs.valueOf(OPERATOR) else OPERATOR.default()
        }
    }


    fun getPrefix(): String {
        return query<String>(select(ID.sqlEquals(guildId), ID, PREFIX)) { rs ->
            if (rs.next()) rs.valueOf(PREFIX) else Volte.config().prefix()
        }
    }

    fun getAutoQuote(): Boolean {
        return query<Boolean>(select(ID.sqlEquals(guildId), ID, AUTOQUOTE)) { rs ->
            if (rs.next()) rs.valueOf(AUTOQUOTE) else AUTOQUOTE.default()
        }
    }

    fun getAutorole(): String {
        return query<String>(select(ID.sqlEquals(guildId), ID, AUTOROLE)) { rs ->
            return@query if (rs.next()) rs.valueOf(AUTOROLE) else AUTOROLE.default()
        }
    }


    override fun getPrefixes(): MutableCollection<String> {
        return mutableListOf(getPrefix())
    }
}

