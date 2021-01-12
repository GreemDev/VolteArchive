package volte.lib.db.columns

import volte.lib.db.SQLColumn
import volte.meta.updateValueOf
import volte.meta.valueOf
import java.sql.ResultSet

class LongColumn(
    name: String,
    nullable: Boolean,
    private val default: Long = 0L,
    private val primaryKey: Boolean = false
) : SQLColumn<Long>(name, nullable, default, primaryKey) {
    override fun sqlSpec(): String {
        return "BIGINT DEFAULT $default ${nullableStr()} ${if (primaryKey) " PRIMARY KEY" else ""}"
    }

    override fun getValue(rs: ResultSet): Long = rs.valueOf(this)

    override fun updateValue(rs: ResultSet, new: Long) = rs.updateValueOf(this, new)
}