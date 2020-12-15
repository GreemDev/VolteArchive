package volte.database

import com.jagrosh.easysql.DatabaseConnector
import volte.Volte
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class VolteDatabase {

    val conn = connector.connection

    companion object {
        val connector: DatabaseConnector = DatabaseConnector("./data/volte", null, null)

        fun createNew(): VolteDatabase = VolteDatabase()
        fun createRawConnection(): Connection = DriverManager.getConnection("jdbc:h2:./data/volte")
    }

    fun currentConnection(): Connection = conn
    fun createStatement(): Statement = conn.createStatement()
    fun dbConnector(): DatabaseConnector = connector

    fun getRecordsFor(guildId: String): ResultSet = currentConnection().prepareStatement("SELECT * FROM guilds WHERE id = ?").also {
        it.setString(1, guildId)
    }.executeQuery()
    fun getWelcomeSettingsFor(guildId: String): WelcomeSettings = WelcomeSettings(this, guildId)
    fun getAllSettingsFor(guildId: String): GuildData = GuildData(guildId, this)
    fun getTagsFor(guildId: String): TagsRepository = TagsRepository(this, guildId)


    fun initializeDb() {
        val statement = createStatement()

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS guilds (id varchar(20) primary key, autorole varchar(20) not null, operator varchar(20) not null, prefix varchar not null)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS tags (id int auto_increment primary key, guildId varchar(20) not null, name varchar not null, content varchar not null, creator varchar(20) not null, uses int not null)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS welcome (id varchar(20) primary key, channel varchar(20) not null, joinMessage varchar not null, leaveMessage varchar not null, color varchar not null, dm varchar not null)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS volte_meta (id int primary key, version varchar(5))")

        val rs = statement.executeQuery("SELECT * FROM volte_meta")
        if (rs.next().not()) {
            statement.executeUpdate("INSERT INTO volte_meta VALUES(1, '4.0.0')")
        }




        statement.queryTimeout = 30

        Volte.jda().guilds.forEach {
            val result = statement.executeQuery("SELECT * FROM guilds WHERE id = '${it.id}'")
            if (result.next().not()) {
                statement.executeUpdate("INSERT INTO guilds VALUES('${it.id}', '', '', '${Volte.config().prefix()}')")
                Volte.logger().info("Added ${it.name} to the database.")
            }
        }
    }
}