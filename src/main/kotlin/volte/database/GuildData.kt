package volte.database

import volte.entities.VolteTag
import volte.modules.WelcomeModule
import java.awt.Color
import java.sql.Connection
import java.sql.ResultSet
import java.util.*
import kotlin.collections.HashSet

class GuildData(private val rs: ResultSet) {

    fun resultSet() = rs

    private val welcome: WelcomeSettings = WelcomeSettings.createNew(id())

    fun id(): String = rs.getString("id")
    fun operator(): String = rs.getString("operator")
    fun prefix(): String = rs.getString("prefix")
    fun autorole(): String = rs.getString("autorole")
    fun owner(): String = rs.getString("owner")
    fun welcome(): WelcomeSettings = WelcomeSettings.createNew(id())

}

class WelcomeSettings(private val rs: ResultSet) {
    companion object {
        fun createNew(id: String): WelcomeSettings {
            val db = VolteDatabase.createNew()
            val statement = db.createStatement()
            val rs = statement.executeQuery("SELECT 1 FROM welcome WHERE id = $id")
            db.closeConnection()
            return WelcomeSettings(rs)
        }
    }

    fun id(): String = rs.getString("id")
    fun channel(): String = rs.getString("channel")
    fun greeting(): String = rs.getString("joinMessage")
    fun farewell(): String = rs.getString("leaveMessage")
    fun color(): Color = WelcomeModule.parseColor(rs.getString("color"))
    fun dmGreeting(): String = rs.getString("dm")

}

class TagsRepository(private val rs: ResultSet) {

    private val tags: HashSet<VolteTag> = hashSetOf()
    fun tags(): HashSet<VolteTag> = tags

    init {
        while (rs.next()) {
            tags.add(VolteTag(rs))
        }
    }

    companion object {
        fun createNew(id: String): TagsRepository {
            val db = VolteDatabase.createNew()
            val statement = db.createStatement()
            val rs = statement.executeQuery("SELECT 1 FROM tags WHERE guildId = $id")
            return TagsRepository(rs)
        }
    }




}