package volte.entities

import com.jagrosh.easysql.SQLColumn
import com.jagrosh.easysql.columns.IntegerColumn
import com.jagrosh.easysql.columns.StringColumn
import java.sql.ResultSet

data class VolteTag(private val rs: ResultSet) {

    companion object {
        val GUILD: SQLColumn<String> = StringColumn("guildId", false, "", 20)
        val NAME: SQLColumn<String> = StringColumn("name", false, "", 20)
        val CONTENT: SQLColumn<String> = StringColumn("content", false, "", 1950)
        val USES: SQLColumn<Int> = IntegerColumn("uses", false, 1)
        val CREATOR: SQLColumn<String> = StringColumn("creator", false, "", 20)
    }


    private val name: String = rs.getString("name")
    private val content: String = rs.getString("content")
    private val uses: Int = rs.getInt("uses")
    private val creatorId: String = rs.getString("creator")

    fun name() = name
    fun content() = content
    fun uses() = uses
    fun creatorId() = creatorId

}