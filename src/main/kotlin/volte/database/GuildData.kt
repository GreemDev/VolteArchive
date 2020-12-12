package volte.database

import volte.entities.VolteTag
import volte.modules.WelcomeModule
import java.awt.Color
import java.sql.ResultSet
import kotlin.collections.HashSet

class GuildData(private val rs: ResultSet, private val db: VolteDatabase) {

    fun resultSet() = rs

    private val id: String
    private val operator: String
    private val prefix: String
    private val autorole: String
    private val welcome: WelcomeSettings
    private val tagRepo: TagsRepository

    fun close() {
        db.closeConnection()
    }

    init {
        id = rs.getInt("id").toString()
        operator = rs.getString("operator")
        prefix = rs.getString("prefix")
        autorole = rs.getString("autorole")

        welcome = db.getWelcomeSettingsFor(id)
        tagRepo = db.getTagsFor(id)
    }

    fun id(): String = id
    fun operator(): String = operator
    fun prefix(): String = prefix
    fun autorole(): String = autorole
    fun welcome(): WelcomeSettings = welcome
    fun tagRepo(): TagsRepository = tagRepo

}

class WelcomeSettings(rs: ResultSet, db: VolteDatabase) {
    companion object {
        fun createNew(id: String, db: VolteDatabase): WelcomeSettings {
            val statement = db.createStatement()
            val rs = statement.executeQuery("SELECT 1 FROM welcome WHERE id = $id")
            return WelcomeSettings(rs, db)
        }
    }

    val id: String
    val channel: String
    val greeting: String
    val farewell: String
    val color: Color
    val dmGreeting: String

    init {
        id = rs.getString("id")
        channel = rs.getString("channel")
        greeting = rs.getString("joinMessage")
        farewell = rs.getString("leaveMessage")
        color = WelcomeModule.parseColor(rs.getString("color"))
        dmGreeting = rs.getString("dm")
    }




    fun id(): String = id
    fun channel(): String = channel
    fun greeting(): String = greeting
    fun farewell(): String = farewell
    fun color(): Color = color
    fun dmGreeting(): String = dmGreeting

}

class TagsRepository(rs: ResultSet) {

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