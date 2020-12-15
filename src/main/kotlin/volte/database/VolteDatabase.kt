package volte.database

import com.jagrosh.easysql.DatabaseConnector
import volte.Volte
import volte.database.entities.*

class VolteDatabase {
    companion object {
        private val connector: DatabaseConnector = DatabaseConnector("./data/volte", null, null)
    }

    fun connector(): DatabaseConnector = connector

    fun getWelcomeSettingsFor(guildId: String): WelcomeSettings = WelcomeSettings(this, guildId)
    fun getAllSettingsFor(guildId: String): GuildData = GuildData(guildId, this)
    fun getTagsFor(guildId: String): TagsRepository = TagsRepository(this, guildId)


    fun initializeDb() {
        val statement = connector.connection.createStatement()

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS GUILDS (ID VARCHAR(20) PRIMARY KEY, AUTOROLE VARCHAR(20) NOT NULL, OPERATOR VARCHAR(20) NOT NULL, PREFIX VARCHAR NOT NULL, MASSPINGS BOOLEAN NOT NULL, ANTILINK BOOLEAN NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS TAGS (ID int AUTO_INCREMENT PRIMARY KEY, GUILDID VARCHAR(20) NOT NULL, NAME VARCHAR NOT NULL, CONTENT VARCHAR NOT NULL, CREATOR VARCHAR(20) NOT NULL, USES INT NOT NULL)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS WELCOME (ID VARCHAR(20) PRIMARY KEY, CHANNEL VARCHAR(20) NOT NULL, JOINMESSAGE VARCHAR NOT NULL, LEAVEMESSAGE VARCHAR NOT NULL, COLOR VARCHAR NOT NULL, DM VARCHAR NOT NULL\n)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS VOLTE_META (ID INT PRIMARY KEY, VERSION VARCHAR(5))")

        val rs = statement.executeQuery("SELECT * FROM VOLTE_META")
        if (rs.next().not()) {
            statement.executeUpdate("INSERT INTO VOLTE_META VALUES(1, '4.0.0')")
        }


        statement.queryTimeout = 30

        Volte.jda().guilds.forEach {
            val result = statement.executeQuery("SELECT * FROM GUILDS WHERE ID = '${it.id}'")
            if (result.next().not()) {
                statement.executeUpdate("INSERT INTO GUILDS VALUES('${it.id}', '', '', '${Volte.config().prefix()}', FALSE, FALSE)")
                Volte.logger().info("Added ${it.name} to the database.")
            }
        }
    }
}