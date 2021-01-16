package volte.database.entities

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import volte.Volte
import volte.lib.db.DbManager
import volte.lib.db.DbColumn
import volte.lib.db.columns.StringColumn
import volte.lib.meta.valueOf
import volte.lib.meta.DiscordUtil
import java.awt.Color

data class WelcomeSettings(val guildId: String) : DbManager(Volte.db().connection(), "WELCOME") {

    companion object {
        val ID: DbColumn<String> = StringColumn("ID", false, maxLength = 20)
        val CHANNEL: DbColumn<String> = StringColumn("CHANNEL", false, maxLength = 20)
        val GREETING: DbColumn<String> = StringColumn("JOINMESSAGE", false, maxLength = 1950)
        val FAREWELL: DbColumn<String> = StringColumn("LEAVEMESSAGE", false, maxLength = 1950)
        val COLOR: DbColumn<String> = StringColumn("COLOR", false, "251;0;112", maxLength = 11)
        val DMGREETING: DbColumn<String> = StringColumn("DM", false, maxLength = 1950)
    }

    constructor() : this("")

    override fun allColumns(): List<DbColumn<*>> {
        return listOf(ID, CHANNEL, GREETING, FAREWELL, COLOR, DMGREETING)
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
        return query<String>(select(ID.sqlEquals(guildId), ID, CHANNEL)) { rs ->
            if (rs.next()) rs.valueOf(CHANNEL) else CHANNEL.default()
        }
    }

    fun getGreeting(): String {
        return query<String>(select(ID.sqlEquals(guildId), ID, GREETING)) { rs ->
            if (rs.next()) rs.valueOf(GREETING) else GREETING.default()
        }
    }

    fun getFarewell(): String {
        return query<String>(select(ID.sqlEquals(guildId), ID, FAREWELL)) { rs ->
            if (rs.next()) rs.valueOf(FAREWELL) else FAREWELL.default()
        }
    }

    fun getColor(): Color {
        return DiscordUtil.parseColor(query<String>(select(ID.sqlEquals(guildId), ID, COLOR)) { rs ->
            if (rs.next()) rs.valueOf(COLOR) else COLOR.default()
        })
    }

    fun getDmGreeting(): String {
        return query<String>(select(ID.sqlEquals(guildId), ID, DMGREETING)) { rs ->
            if (rs.next()) rs.valueOf(DMGREETING) else DMGREETING.default()
        }
    }

    fun setChannel(channelId: String) {
        modify(
            update(ID.sqlEquals(guildId), hashMapOf(
                CHANNEL to channelId
            ))
        )
    }

    fun setGreeting(greeting: String) {
        modify(
            update(ID.sqlEquals(guildId), hashMapOf(
                GREETING to greeting
            ))
        )
    }

    fun setFarewell(farewell: String) {
        modify(
            update(ID.sqlEquals(guildId), hashMapOf(
                FAREWELL to farewell
            ))
        )
    }

    fun setColor(color: String) {
        modify(
            update(ID.sqlEquals(guildId), hashMapOf(
                COLOR to color
            ))
        )
    }

    fun setDmGreeting(dmGreeting: String) {
        modify(
            update(ID.sqlEquals(guildId), hashMapOf(
                DMGREETING to dmGreeting
            ))
        )
    }


    fun id(): String = guildId

}