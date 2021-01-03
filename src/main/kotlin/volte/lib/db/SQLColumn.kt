package volte.lib.db

import java.sql.ResultSet
import java.sql.SQLException

/**
 * Base class for Volte SQL Column implementations.
 * [T] directly correlates to the type of data being stored in the column,
 * so non-SQL supported types such as [java.time.Instant] would require parsing.
 *
 * See [volte.lib.db.columns.InstantColumn] for an example
 */
abstract class SQLColumn<T> {

    private val name: String
    private val nullable: Boolean
    private val default: T
    private val primaryKey: Boolean


    constructor(name: String, nullable: Boolean, default: T) {
        this.name = name
        this.nullable = nullable
        this.default = default
        this.primaryKey = false
    }

    constructor(name: String, nullable: Boolean, default: T, primaryKey: Boolean) {
        this.name = name
        this.nullable = nullable
        this.default = default
        this.primaryKey = primaryKey
    }

    protected fun nullableStr(): String {
        return if (nullable) "" else " NOT NULL"
    }

    fun default() = default
    fun name() = name

    fun equalsValue(value: T): String {
        return if (value is String) {
            "$name = '$value'"
        } else "$name = $value"
    }

    infix fun eq(value: T): String {
        return equalsValue(value)
    }

    fun lessThan(value: Long): String {
        return "$name < $value"
    }

    fun greaterThan(value: Long): String {
        return "$name > $value"
    }

    override fun toString(): String = dataDescription()

    abstract fun dataDescription(): String

    @Throws(SQLException::class)
    abstract fun getValue(rs: ResultSet): T

    @Throws(SQLException::class)
    abstract fun updateValue(rs: ResultSet, new: T)

}
