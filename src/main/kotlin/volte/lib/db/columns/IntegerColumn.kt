package volte.lib.db.columns

import volte.lib.db.DbColumn
import volte.lib.meta.updateValueOf
import volte.lib.meta.valueOf
import java.sql.ResultSet

class IntegerColumn(
    name: String,
    nullable: Boolean,
    private val default: Int = 0,
    private val autoIncrement: Boolean = false,
    private val primaryKey: Boolean = false
) : DbColumn<Int>(name, nullable, default) {

    override fun sqlSpec(): String = "INT ${if (autoIncrement) "AUTO_INCREMENT" else "DEFAULT $default ${nullableStr()}"}${if (primaryKey) " PRIMARY KEY" else ""}"

    override fun getValue(rs: ResultSet): Int = rs.valueOf(this)

    override fun updateValue(rs: ResultSet, new: Int) = rs.updateValueOf(this, new)
}