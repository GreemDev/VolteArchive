package volte.entities

import java.sql.ResultSet

data class VolteTag(private val rs: ResultSet) {

    private val name: String = rs.getString("name")
    private val content: String = rs.getString("content")
    private val uses: Int = rs.getInt("uses")
    private val creatorId: String = rs.getString("creator")

    fun name() = name
    fun content() = content
    fun uses() = uses
    fun creatorId() = creatorId

}