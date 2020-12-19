package volte.database

import com.jagrosh.easysql.DatabaseConnector
import volte.Volte
import volte.database.entities.*
import volte.meta.Version

class VolteDatabase {
    companion object {
        private val connector: DatabaseConnector = generateConnector()

        private fun generateConnector(): DatabaseConnector {
            return DatabaseConnector("./data/volte", null, null)/*.also {
                val field = it::class.java.getDeclaredField("LOG")
                if (field.trySetAccessible()) {
                    field.set(this, Volte.logger() as org.slf4j.Logger)
                }
            }*/
        }
    }

    fun connector(): DatabaseConnector = connector

    fun getWelcomeSettingsFor(guildId: String): WelcomeSettings = WelcomeSettings(guildId)
    fun getBlacklistFor(guildId: String): BlacklistRepository = BlacklistRepository(guildId)
    fun getSettingsFor(guildId: String): GuildData = GuildData(guildId)
    fun getTagsFor(guildId: String): TagsRepository = TagsRepository(guildId)
    fun getSelfRolesFor(guildId: String): SelfRoleRepository = SelfRoleRepository(guildId)


    fun initializeDb() {
        val statement = connector.connection.createStatement()

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS GUILDS (ID VARCHAR(20) PRIMARY KEY, AUTOROLE VARCHAR(20) NOT NULL, OPERATOR VARCHAR(20) NOT NULL, PREFIX VARCHAR NOT NULL, MASSPINGS BOOLEAN NOT NULL, ANTILINK BOOLEAN NOT NULL, AUTOQUOTE BOOLEAN NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS TAGS (ID INT AUTO_INCREMENT PRIMARY KEY, GUILDID VARCHAR(20) NOT NULL, NAME VARCHAR NOT NULL, CONTENT VARCHAR NOT NULL, CREATOR VARCHAR(20) NOT NULL, USES INT NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS WELCOME (ID VARCHAR(20) PRIMARY KEY, CHANNEL VARCHAR(20) NOT NULL, JOINMESSAGE VARCHAR NOT NULL, LEAVEMESSAGE VARCHAR NOT NULL, COLOR VARCHAR NOT NULL, DM VARCHAR NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS SELFROLES (ID INT AUTO_INCREMENT PRIMARY KEY, GUILDID VARCHAR(20) NOT NULL, ROLEID VARCHAR(20) NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS BLACKLIST (ID INT AUTO_INCREMENT PRIMARY KEY, GUILDID VARCHAR(20) NOT NULL, PHRASE VARCHAR(200) NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS VOLTE_META (ID INT PRIMARY KEY, VERSION VARCHAR(20))")

        val rs = statement.executeQuery("SELECT * FROM VOLTE_META")
        if (rs.next().not()) {
            statement.executeUpdate("INSERT INTO VOLTE_META VALUES(1, '${Version.formatted()}')")
        }

        statement.queryTimeout = 30

        Volte.jda().guilds.forEach {
            val guildSet = statement.executeQuery("SELECT * FROM GUILDS WHERE ID = '${it.id}'")
            if (!guildSet.next()) {
                statement.executeUpdate("INSERT INTO GUILDS VALUES('${it.id}', '', '', '${Volte.config().prefix()}', FALSE, FALSE, FALSE)")
                Volte.logger().info("Added ${it.name} to the guilds table.")
            }

            val welcomeSet = statement.executeQuery("SELECT * FROM WELCOME WHERE ID = '${it.id}'")
            if (!welcomeSet.next()) {
                statement.executeUpdate("INSERT INTO WELCOME VALUES ('${it.id}', '', '', '', '251;0;112', '')")
                Volte.logger().info("Added ${it.name} to the guilds welcome table.")
            }

        }
    }
}