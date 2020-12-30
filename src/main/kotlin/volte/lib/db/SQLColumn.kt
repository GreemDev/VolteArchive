package volte.lib.db

import java.sql.ResultSet
import java.sql.SQLException
import kotlin.jvm.Throws

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

    fun defaultVal() = default
    fun name() = name

    fun equalsValue(value: String): String {
        return "$name = '$value'"
    }

    fun equalsValue(value: Long): String {
        return equalsValue(value.toString())
    }

    fun equalsValue(value: Int): String {
        return equalsValue(value.toString())
    }

    fun lessThan(value: Long): String {
        return "$name < $value"
    }

    fun greaterThan(value: Long): String {
        return "$name > $value"
    }

    override fun toString(): String {
        return name
    }

    abstract fun dataDescription(): String
    @Throws(SQLException::class)
    abstract fun getValue(rs: ResultSet): T

    @Throws(SQLException::class)
    abstract fun updateValue(rs: ResultSet, new: T)

}
