package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.StringColumn
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import volte.database.VolteDatabase
import volte.meta.equalsValue
import volte.util.DiscordUtil
import java.awt.Color

class WelcomeSettings(db: VolteDatabase, private val guildId: String): DataManager(db.connector(), "welcome") {

    companion object {
        val ID: SQLColumn<String> = StringColumn("id", false, "", 20)
        val CHANNEL: SQLColumn<String> = StringColumn("channel", false, "", 20)
        val GREETING: SQLColumn<String> = StringColumn("joinMessage", false, "", 1950)
        val FAREWELL: SQLColumn<String> = StringColumn("leaveMessage", false, "", 1950)
        val COLOR: SQLColumn<String> = StringColumn("color", false, "251,0,112", 11)
        val DMGREETING: SQLColumn<String> = StringColumn("dm", false, "", 1950)
    }

    fun replacePlaceholders(text: String, user: User, guild: Guild): String {
        return text.replace("{GuildName}", guild.name, true)
            .replace("{MemberName}", user.name, true)
            .replace("{MemberMention}", user.asMention, true)
            .replace("{OwnerMention}", "<@${guild.ownerId}>", true)
            .replace("{MemberTag}", user.discriminator, true)
            .replace("{MemberCount}", "${guild.memberCount}", true)
            .replace("{MemberString}", user.asTag)
    }

    fun getChannel(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, CHANNEL)) { rs ->
            if (rs.next()) CHANNEL.getValue(rs) else ""
        }
    }

    fun getGreeting(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, GREETING)) { rs ->
            if (rs.next()) GREETING.getValue(rs) else ""
        }
    }

    fun getFarewell(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, FAREWELL)) { rs ->
            if (rs.next()) FAREWELL.getValue(rs) else ""
        }
    }

    fun getColor(): Color {
        return DiscordUtil.parseColor(read<String>(select(ID.equalsValue(guildId), ID, COLOR)) { rs ->
            if (rs.next()) COLOR.getValue(rs) else COLOR.defaultValue
        })
    }

    fun getDmGreeting(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, DMGREETING)) { rs ->
            if (rs.next()) DMGREETING.getValue(rs) else ""
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