package volte.database

import volte.Volte
import java.sql.*

class VolteDatabase private constructor(connection: Connection? = null) {

    private val conn: Connection

    init {
        conn = connection ?: createNewConnection()
    }

    companion object {
        fun createNew(): VolteDatabase = VolteDatabase()
        fun createNew(conn: Connection): VolteDatabase = VolteDatabase(conn)
    }

    private fun createNewConnection(): Connection = DriverManager.getConnection("jdbc:sqlite:data/volte.db")
    fun currentConnection(): Connection = conn
    fun closeConnection() = currentConnection().close()

    fun getStringFor(guildId: String, columnName: String): String {
        val rs = getRecordsFor(guildId)
        return rs.getString(columnName).also {
            closeConnection()
        }
    }

    fun getRecordsFor(guildId: String): ResultSet {
        val statement = conn.prepareStatement("SELECT * FROM guilds WHERE id = $guildId")
        return statement.executeQuery()
    }

    fun getWelcomeSettingsFor(guildId: String): WelcomeSettings {
        return WelcomeSettings.createNew(guildId, this)
    }

    fun getAllSettingsFor(guildId: String): GuildData {
        val statement = conn.prepareStatement("SELECT * FROM guilds WHERE id = $guildId")
        return GuildData(statement.executeQuery(), this)
    }

    fun getTagsFor(guildId: String): TagsRepository {
        return TagsRepository.createNew(guildId)
    }

    fun createStatement(): Statement = conn.createStatement()


    fun initializeDb() {
        val statement = conn.createStatement()

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS guilds (id integer primary key, autorole string not null, operator string not null, prefix string not null)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS tags (id integer auto_increment primary key, guildId string not null, name string not null, content string not null, creator string not null, uses integer not null)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS welcome (id integer primary key, channel string not null, joinMessage string not null, leaveMessage string not null, color string not null, dm string not null)")
        //statement.executeUpdate("CREATE TABLE IF NOT EXISTS guild_settings (id integer primary key)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS volte_meta (id integer primary key, version string)")

        try {
            statement.executeUpdate("INSERT INTO volte_meta VALUES(1, '4.0.0.0')")
        } catch (ignored: SQLException) {

        }




        try {
            statement.queryTimeout = 30

            Volte.jda().guilds.forEach {
                val result = statement.executeQuery("SELECT 1 FROM guilds WHERE id = ${it.id}")
                if (result.next().not()) {
                    statement.executeUpdate("INSERT INTO guilds values(${it.id}, '', '', '${Volte.config().prefix()}')")
                    Volte.logger().info("Added ${it.name} to the database.")
                }
            }
        } catch (e: SQLException) {
            Volte.logger().error(e.message)
        } finally {
            conn.close()
        }
    }
}