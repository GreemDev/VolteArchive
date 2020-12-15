package volte.database

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.StringColumn
import volte.Volte
import volte.entities.VolteTag
import volte.meta.equalsValue
import volte.modules.WelcomeModule
import volte.util.DiscordUtil
import java.awt.Color
import kotlin.collections.HashSet

class GuildData(private val guildId: String, db: VolteDatabase) : DataManager(db.dbConnector(), "guilds") {

    companion object {
        val ID: SQLColumn<String> = StringColumn("id", false, "", 20)
        val OPERATOR: SQLColumn<String> = StringColumn("operator", false, "", 20)
        val PREFIX: SQLColumn<String> = StringColumn("prefix", false, Volte.config().prefix(), 20)
        val AUTOROLE: SQLColumn<String> = StringColumn("autorole", false, "", 20)
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

class WelcomeSettings(db: VolteDatabase, private var guildId: String): DataManager(db.dbConnector(), "welcome") {

    companion object {
        val ID: SQLColumn<String> = StringColumn("id", false, "", 20)
        val CHANNEL: SQLColumn<String> = StringColumn("channel", false, "", 20)
        val GREETING: SQLColumn<String> = StringColumn("joinMessage", false, "", 1950)
        val FAREWELL: SQLColumn<String> = StringColumn("leaveMessage", false, "", 1950)
        val COLOR: SQLColumn<String> = StringColumn("color", false, "251,0,112", 11)
        val DMGREETING: SQLColumn<String> = StringColumn("dm", false, "", 1950)
    }

    fun getChannel(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, CHANNEL)) { rs ->
            if (rs.next())
                CHANNEL.getValue(rs)
            else ""
        }
    }

    fun getGreeting(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, GREETING)) { rs ->
            if (rs.next())
                GREETING.getValue(rs)
            else ""
        }
    }

    fun getFarewell(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, FAREWELL)) { rs ->
            if (rs.next()) {
                FAREWELL.getValue(rs)
            } else ""
        }
    }

    fun getColor(): Color {
        return DiscordUtil.parseColor(read<String>(select(ID.equalsValue(guildId), ID, COLOR)) { rs ->
            if (rs.next()) {
                COLOR.getValue(rs)
            } else COLOR.defaultValue
        })
    }

    fun getDmGreeting(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, DMGREETING)) { rs ->
            if (rs.next())
                DMGREETING.getValue(rs)
            else ""
        }
    }

    fun setChannel(channelId: String) {
        readWrite(select(ID.equalsValue(guildId), ID, CHANNEL)) { rs ->
            if (rs.next()) {
                CHANNEL.updateValue(rs, channelId)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                CHANNEL.updateValue(rs, channelId)
                rs.insertRow()
            }
        }
    }

    fun setGreeting(greeting: String) {
        readWrite(select(ID.equalsValue(guildId), ID, GREETING)) { rs ->
            if (rs.next()) {
                GREETING.updateValue(rs, greeting)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                GREETING.updateValue(rs, greeting)
                rs.insertRow()
            }
        }
    }

    fun setFarewell(farewell: String) {
        readWrite(select(ID.equalsValue(guildId), ID, FAREWELL)) { rs ->
            if (rs.next()) {
                FAREWELL.updateValue(rs, farewell)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                FAREWELL.updateValue(rs, farewell)
                rs.insertRow()
            }
        }
    }

    fun setColor(color: String) {
        readWrite(select(ID.equalsValue(guildId), ID, COLOR)) { rs ->
            if (rs.next()) {
                COLOR.updateValue(rs, color)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                COLOR.updateValue(rs, color)
                rs.insertRow()
            }
        }
    }

    fun setDmGreeting(dmGreeting: String) {
        readWrite(select(ID.equalsValue(guildId), ID, DMGREETING)) { rs ->
            if (rs.next()) {
                DMGREETING.updateValue(rs, dmGreeting)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                ID.updateValue(rs, guildId)
                DMGREETING.updateValue(rs, dmGreeting)
                rs.insertRow()
            }
        }
    }


    fun id(): String = guildId

}

class TagsRepository(db: VolteDatabase, guildId: String): DataManager(db.dbConnector(), "tags") {

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