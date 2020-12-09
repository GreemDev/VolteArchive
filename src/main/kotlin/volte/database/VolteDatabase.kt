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

    fun createNewConnection(): Connection = DriverManager.getConnection("jdbc:sqlite:data/volte.db")
    fun currentConnection(): Connection = conn

    fun getStringFor(guildId: String, columnName: String): String {
        val rs = getRecordsFor(guildId)
        return rs.getString(columnName)
    }

    fun getRecordsFor(guildId: String): ResultSet {
        val statement = conn.prepareStatement("SELECT 1 FROM guilds WHERE id = $guildId")
        return statement.executeQuery()
    }

    fun getWelcomeSettingsFor(guildId: String): ResultSet {
        val statement = conn.prepareStatement("SELECT 1 FROM welcome WHERE id = $guildId")
        return statement.executeQuery()
    }

    fun getTagsFor(guildId: String): ResultSet {
        val statement = conn.prepareStatement("SELECT 1 FROM tags WHERE id = $guildId")
        return statement.executeQuery()
    }

    fun createStatement(): Statement = conn.createStatement()


    fun initializeDb(volte: Volte) {
        val statement = conn.createStatement()

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS guilds (id integer primary key, autorole string, owner integer, operator string)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS tags (id integer primary key, name string, content string, creator string, uses integer)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS welcome (id integer primary key, channel string, joinMessage string, leaveMessage string, color string, dm string)")
        //statement.executeUpdate("CREATE TABLE IF NOT EXISTS guild_settings (id integer primary key)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS volte_meta (id integer primary key, version string)")

        try {
            statement.executeUpdate("UPDATE volte_meta SET version = '4.0.0.0' WHERE id = 1")
        } catch (e: SQLException) {
            statement.executeUpdate("INSERT INTO volte_meta VALUES('4.0.0.0')")
        }




        try {
            statement.queryTimeout = 30

            volte.jda().guilds.forEach {
                val result = statement.executeQuery("SELECT * FROM guilds WHERE id = ${it.id}")
                if (result.next().not()) {
                    statement.executeUpdate("INSERT INTO guilds values(${it.id}, '', ${it.ownerId}, '')")
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