package volte.database

import volte.Volte
import volte.database.entities.*
import volte.lib.db.DatabaseConnector
import volte.meta.Version
import volte.meta.fromEnd
import volte.meta.valueOf

class VolteDatabase {
    companion object {
        private lateinit var connector: DatabaseConnector
    }

    init {
        connector = DatabaseConnector("./data/volte")
    }

    fun connector(): DatabaseConnector = connector

    fun getWelcomeSettingsFor(guildId: String): WelcomeSettings = WelcomeSettings(guildId)
    fun getBlacklistFor(guildId: String): BlacklistRepository = BlacklistRepository(guildId)
    fun getSettingsFor(guildId: String): GuildData = GuildData(guildId)

    fun getTagsFor(guildId: String): TagsRepository = TagsRepository(guildId)
    fun getSelfRolesFor(guildId: String): SelfRoleRepository = SelfRoleRepository(guildId)

    fun initializeDb() {
        val statement = connector.connection().createStatement()
        for (manager in arrayListOf(GuildData(""), WelcomeSettings(""))) {
            statement.executeUpdate(buildString("CREATE TABLE IF NOT EXISTS ${manager.tableName} (") {

                manager.allColumns().forEachIndexed { index, column ->
                    var res = "${column.name()} ${column.sqlSpec()}, "
                    if (index == manager.allColumns().size.dec()) {
                        res = res.fromEnd { substring(2) }
                    }

                    append(res)
                }

                append(")")
            })
        }

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS TAGS (ID INT AUTO_INCREMENT PRIMARY KEY, GUILDID VARCHAR(20) NOT NULL, NAME VARCHAR NOT NULL, CONTENT VARCHAR NOT NULL, CREATOR VARCHAR(20) NOT NULL, USES INT NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS SELFROLES (ID INT AUTO_INCREMENT PRIMARY KEY, GUILDID VARCHAR(20) NOT NULL, ROLEID VARCHAR(20) NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS BLACKLIST (ID INT AUTO_INCREMENT PRIMARY KEY, GUILDID VARCHAR(20) NOT NULL, PHRASE VARCHAR(200) NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS VOLTE_META (ID INT PRIMARY KEY, VERSION VARCHAR(20))")

        val rs = statement.executeQuery("SELECT * FROM VOLTE_META")
        if (!rs.next()) {
            statement.executeUpdate("INSERT INTO VOLTE_META VALUES(1, '${Version.formatted()}')")
        }

        val allGuilds = arrayListOf<String>().apply {
            val set = statement.executeQuery("SELECT * FROM GUILDS")
            while (set.next()) {
                add(set.valueOf(GuildData.ID))
            }
        }

        val allWelcomeSettings = arrayListOf<String>().apply {
            val set = statement.executeQuery("SELECT * FROM WELCOME")
            while (set.next()) {
                add(set.valueOf(WelcomeSettings.ID))
            }
        }

        Volte.jda().guilds.forEach { guild ->
            if (!allGuilds.contains(guild.id)) {
                statement.executeUpdate(
                    buildString("INSERT INTO GUILDS VALUES (") {
                        append("'${guild.id}', ")
                        append("${GuildData.OPERATOR.formattedDefault()}, ")
                        append("'${Volte.config().prefix()}', ")
                        append("${GuildData.MASSPINGS.default()}, ")
                        append("${GuildData.ANTILINK.default()}, ")
                        append("${GuildData.AUTOQUOTE.default()})")
                    })
                Volte.logger().info("Added ${guild.name} to the guilds table.")
            }

            if (!allWelcomeSettings.contains(guild.id)) {
                statement.executeUpdate(
                    buildString("INSERT INTO WELCOME VALUES (") {
                        append("'${guild.id}', ")
                        append("${WelcomeSettings.CHANNEL.formattedDefault()}, ")
                        append("${WelcomeSettings.GREETING.formattedDefault()}, ")
                        append("${WelcomeSettings.FAREWELL.formattedDefault()}, ")
                        append("${WelcomeSettings.COLOR.formattedDefault()}, ")
                        append("${WelcomeSettings.DMGREETING.formattedDefault()})")
                    })
                Volte.logger().info("Added ${guild.name} to the guilds welcome table.")
            }

        }
    }
}