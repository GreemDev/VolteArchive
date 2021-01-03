package volte.lib.db.columns

import volte.lib.db.SQLColumn
import java.sql.ResultSet

class StringColumn(
    private val name: String,
    nullable: Boolean,
    private val default: String = "",
    private val primaryKey: Boolean = false,
    private val maxLength: Int = 0
) : SQLColumn<String>(name, nullable, default, primaryKey) {

    override fun dataDescription(): String {
        return "VARCHAR${if (maxLength == 0) " " else "($maxLength) "} DEFAULT '$default' ${nullableStr()} ${if (primaryKey) " PRIMARY KEY" else ""}"
    }

    override fun getValue(rs: ResultSet): String {
        return rs.getString(name)
    }

    override fun updateValue(rs: ResultSet, new: String) {
        return rs.updateString(name, new)
    }
}