package volte.database

import volte.entities.VolteTag
import volte.modules.WelcomeModule
import java.awt.Color
import java.sql.ResultSet
import kotlin.collections.HashSet

class GuildData(private val rs: ResultSet, db: VolteDatabase) {

    fun resultSet() = rs

    private val id: String
    private val operator: String
    private val prefix: String
    private val autorole: String
    private val welcome: WelcomeSettings
    private val tagRepo: TagsRepository

    init {
        id = rs.getString("id")
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

class WelcomeSettings(rs: ResultSet) {
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
}