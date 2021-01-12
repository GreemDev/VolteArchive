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
    private val isPrimaryKey: Boolean


    constructor(name: String, nullable: Boolean, default: T) {
        this.name = name
        this.nullable = nullable
        this.default = default
        this.isPrimaryKey = false
    }

    constructor(name: String, nullable: Boolean, default: T, primaryKey: Boolean) {
        this.name = name
        this.nullable = nullable
        this.default = default
        this.isPrimaryKey = primaryKey
    }

    protected fun nullableStr(): String {
        return if (nullable) "" else " NOT NULL"
    }

    fun default() = default
    fun name() = name

    /**
     * Generates the SQL = statement, for use in WHERE statements.
     */
    fun sqlEquals(value: T): String {
        return if (value is String) {
            "$name = '$value'"
        } else "$name = $value"
    }

    fun formattedDefault(): String {
        return if (default is String) {
            "`$default`"
        } else throw IllegalStateException("Cannot call formattedDefault for an SQLColumn that is not a String.")
    }

    /**
     * Infix overload for [sqlEquals]
     */
    infix fun eq(value: T): String {
        return sqlEquals(value)
    }

    fun sqlLessThan(value: Long): String {
        return "$name < $value"
    }

    fun sqlGreaterThan(value: Long): String {
        return "$name > $value"
    }

    override fun toString(): String = sqlSpec()

    /**
     * Returns the raw SQL representing this [SQLColumn]. Used for table generation and related things.
     */
    abstract fun sqlSpec(): String

    /**
     * Gets the value of this column on the provided [ResultSet] on the [ResultSet]'s currently selected row.
     */
    @Throws(SQLException::class)
    abstract fun getValue(rs: ResultSet): T

    /**
     * Updates the value of this column on the provided [ResultSet] with the value of [new].
     * This updates the [ResultSet]'s currently selected row.
     */
    @Throws(SQLException::class)
    abstract fun updateValue(rs: ResultSet, new: T)

}
