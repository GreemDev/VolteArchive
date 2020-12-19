package volte.database.entities

import com.jagrosh.easysql.DataManager
import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.StringColumn
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import volte.Volte
import volte.database.VolteDatabase
import volte.meta.equalsValue
import volte.meta.updateValueOf
import volte.meta.valueOf
import volte.util.DiscordUtil
import java.awt.Color

data class WelcomeSettings(val guildId: String): DataManager(Volte.db().connector(), "WELCOME") {

    companion object {
        val ID: SQLColumn<String> = StringColumn("ID", false, "", 20)
        val CHANNEL: SQLColumn<String> = StringColumn("CHANNEL", false, "", 20)
        val GREETING: SQLColumn<String> = StringColumn("JOINMESSAGE", false, "", 1950)
        val FAREWELL: SQLColumn<String> = StringColumn("LEAVEMESSAGE", false, "", 1950)
        val COLOR: SQLColumn<String> = StringColumn("COLOR", false, "251;0;112", 11)
        val DMGREETING: SQLColumn<String> = StringColumn("DM", false, "", 1950)
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

    fun replaceFarewellPlaceholders(user: User, guild: Guild): String {
        return replacePlaceholders(getFarewell(), user, guild)
    }

    fun replaceGreetingPlaceholders(user: User, guild: Guild): String {
        return replacePlaceholders(getGreeting(), user, guild)
    }

    fun getChannel(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, CHANNEL)) { rs ->
            if (rs.next()) rs.valueOf(CHANNEL) else ""
        }
    }

    fun getGreeting(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, GREETING)) { rs ->
            if (rs.next()) rs.valueOf(GREETING) else ""
        }
    }

    fun getFarewell(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, FAREWELL)) { rs ->
            if (rs.next()) rs.valueOf(FAREWELL) else ""
        }
    }

    fun getColor(): Color {
        return DiscordUtil.parseColor(read<String>(select(ID.equalsValue(guildId), ID, COLOR)) { rs ->
            if (rs.next()) rs.valueOf(COLOR) else COLOR.defaultValue
        })
    }

    fun getDmGreeting(): String {
        return read<String>(select(ID.equalsValue(guildId), ID, DMGREETING)) { rs ->
            if (rs.next()) rs.valueOf(DMGREETING) else ""
        }
    }

    fun setChannel(channelId: String) {
        readWrite(select(ID.equalsValue(guildId), ID, CHANNEL)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(CHANNEL, channelId)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(CHANNEL, channelId)
                rs.insertRow()
            }
        }
    }

    fun setGreeting(greeting: String) {
        readWrite(select(ID.equalsValue(guildId), ID, GREETING)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(GREETING, greeting)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(GREETING, greeting)
                rs.insertRow()
            }
        }
    }

    fun setFarewell(farewell: String) {
        readWrite(select(ID.equalsValue(guildId), ID, FAREWELL)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(FAREWELL, farewell)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(FAREWELL, farewell)
                rs.insertRow()
            }
        }
    }

    fun setColor(color: String) {
        readWrite(select(ID.equalsValue(guildId), ID, COLOR)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(COLOR, color)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(COLOR, color)
                rs.insertRow()
            }
        }
    }

    fun setDmGreeting(dmGreeting: String) {
        readWrite(select(ID.equalsValue(guildId), ID, DMGREETING)) { rs ->
            if (rs.next()) {
                rs.updateValueOf(DMGREETING, dmGreeting)
                rs.updateRow()
            } else {
                rs.moveToInsertRow()
                rs.updateValueOf(ID, guildId)
                rs.updateValueOf(DMGREETING, dmGreeting)
                rs.insertRow()
            }
        }
    }


    fun id(): String = guildId

}