package volte.lib.db.columns

import volte.lib.db.SQLColumn
import java.sql.ResultSet

class BooleanColumn(
    private val name: String,
    nullable: Boolean,
    private val default: Boolean = false
    ) : SQLColumn<Boolean>(name, nullable, default) {

    override fun dataDescription(): String = "BOOLEAN DEFAULT ${default.toString().toUpperCase()} ${nullableStr()}"


    override fun getValue(rs: ResultSet): Boolean = rs.getBoolean(name)

    override fun updateValue(rs: ResultSet, new: Boolean) = rs.updateBoolean(name, new)

}