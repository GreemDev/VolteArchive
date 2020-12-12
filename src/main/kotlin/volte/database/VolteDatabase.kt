package volte.database

import volte.Volte
import java.sql.*

class VolteDatabase(connection: Connection? = null) {

    private val conn: Connection = connection ?: createRawConnection()

    companion object {
        fun createNew(): VolteDatabase = VolteDatabase()
        fun createNew(conn: Connection): VolteDatabase = VolteDatabase(conn)
        fun createRawConnection(): Connection = DriverManager.getConnection("jdbc:h2:./data/volte")
    }

    fun currentConnection(): Connection = conn
    fun closeConnection() = currentConnection().close()


    fun getRecordsFor(guildId: String): ResultSet {
        return conn.prepareStatement("SELECT * FROM guilds WHERE id = $guildId").executeQuery()
    }

    fun getWelcomeSettingsFor(guildId: String): WelcomeSettings {
        return WelcomeSettings(conn.prepareStatement("SELECT * FROM welcome WHERE id = '$guildId'").executeQuery())
    }

    fun getAllSettingsFor(guildId: String): GuildData {
        return GuildData(
            conn.prepareStatement("SELECT * FROM guilds WHERE id = $guildId").executeQuery(),
            this
        )
    }

    fun getTagsFor(guildId: String): TagsRepository {
        return TagsRepository(conn.prepareStatement("SELECT * FROM tags WHERE guildId = '$guildId'").executeQuery())
    }

    fun createStatement(): Statement = conn.createStatement()


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




        try {
            statement.queryTimeout = 30

            Volte.jda().guilds.forEach {
                val result = statement.executeQuery("SELECT * FROM guilds WHERE id = '${it.id}'")
                if (result.next().not()) {
                    statement.executeUpdate("INSERT INTO guilds VALUES('${it.id}', '', '', '${Volte.config().prefix()}')")
                    Volte.logger().info("Added ${it.name} to the database.")
                }
            }
        } catch (e: SQLException) {
            Volte.logger().error(e.message)
        } finally {
            statement.close()
        }
    }
}