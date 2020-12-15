package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.BooleanColumn
import com.jagrosh.easysql.columns.StringColumn
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import volte.Volte
import volte.database.VolteDatabase
import volte.database.entities.VolteTag
import volte.meta.equalsValue
import volte.util.DiscordUtil
import java.awt.Color
import kotlin.collections.HashSet

class GuildData(private val guildId: String, db: VolteDatabase) : DataManager(db.connector(), "guilds") {

    companion object {
        val ID: SQLColumn<String> = StringColumn("id", false, "", 20)
        val OPERATOR: SQLColumn<String> = StringColumn("operator", false, "", 20)
        val PREFIX: SQLColumn<String> = StringColumn("prefix", false, Volte.config().prefix(), 20)
        val AUTOROLE: SQLColumn<String> = StringColumn("autorole", false, "", 20)
        val MASSPINGS: SQLColumn<Boolean> = BooleanColumn("massPings", false, false)
        val ANTILINK: SQLColumn<Boolean> = BooleanColumn("antilink", false, false)
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
            if (rs.next())
                OPERATOR.getValue(rs)
            else
                ""
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
            if (rs.next())
                ANTILINK.getValue(rs)
            else false
        }
    }


    fun getMassPings(): Boolean {
        return read<Boolean>(select(ID.equalsValue(guildId), ID, MASSPINGS)) { rs ->
            if (rs.next())
                MASSPINGS.getValue(rs)
            else false
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
            if (rs.next())
                PREFIX.getValue(rs)
            else
                Volte.config().prefix()
        }
    }

    fun getAutorole(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, AUTOROLE)) { rs ->
            if (rs.next())
                AUTOROLE.getValue(rs)
            else ""
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

