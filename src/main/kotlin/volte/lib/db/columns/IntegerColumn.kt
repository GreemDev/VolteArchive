package volte.lib.db.columns

import volte.lib.db.SQLColumn
import volte.meta.updateValueOf
import volte.meta.valueOf
import java.sql.ResultSet

class IntegerColumn(
    name: String,
    nullable: Boolean,
    private val default: Int = 0
) : SQLColumn<Int>(name, nullable, default) {

    override fun sqlSpec(): String = "INTEGER DEFAULT $default ${nullableStr()}"

    override fun getValue(rs: ResultSet): Int = rs.valueOf(this)

    override fun updateValue(rs: ResultSet, new: Int) = rs.updateValueOf(this, new)
}