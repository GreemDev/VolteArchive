package volte.lib.db

import java.sql.Connection
import java.sql.ResultSet

open class DbManager(private val connection: Connection, var tableName: String) {

    init {
        tableName = tableName.toUpperCase()
    }

    open fun allColumns(): List<DbColumn<*>> = emptyList()


    internal fun query(query: String, func: (ResultSet) -> Unit) {
        val rs = connection().prepareStatement(query).executeQuery()

        func(rs)
    }

    internal fun <T> query(query: String, func: (ResultSet) -> T): T {
        val rs = connection().prepareStatement(query).executeQuery()

        return func(rs)
    }

    internal fun modify(statement: String): Int {
        return connection().prepareStatement(statement).executeUpdate()
    }

    internal fun query(query: String): ResultSet = query<ResultSet>(query) { rs -> rs }

    /**
     * Creates a SELECT statement with the given [where] and with the provided [DbColumn]s.
     * If no [DbColumn]s are provided, this returns a SELECT * statement.
     */
    internal fun select(where: String, vararg columns: DbColumn<*> = arrayOf()): String = when {
        columns.isEmpty() -> selectAll(where)
        columns.size == 1 -> select(where, columns[0].name())
        else -> select(where, StringBuilder().apply {
            columns.forEach { col ->
                append("${col.name()}, ")
            }
        }.toString().trim { it == ' ' || it == ',' })
    }

    internal fun selectAll(): String = select(null, "*")

    internal fun selectAll(where: String): String = select(where, "*")

    internal fun whileNext(rs: ResultSet, func: ResultSet.() -> Unit) {
        while (rs.next()) {
            func(rs)
        }
    }

    internal fun delete(where: String): String {
        return "DELETE FROM ${tableName.toUpperCase()} WHERE $where;"
    }

    internal fun insert(valueMappings: Map<DbColumn<*>, Any>): String {
        val toInsert = valueMappings.map { (column, value) ->
            if (column.default() is String) "'$value'" else "$value"
        }
        return "INSERT INTO ${tableName.toUpperCase()} VALUES (${toInsert.joinToString(", ")});"
    }

    internal fun update(where: String, valueMappings: HashMap<DbColumn<*>, Any>): String {
        val toSet = valueMappings.map { (column, value) ->
            "${column.name()} = ${if (column.default() is String) "'$value'" else "$value"}"
        }
        return "UPDATE ${tableName.toUpperCase()} SET ${toSet.joinToString(", ")} WHERE $where;"
    }

    internal fun and(vararg where: String): String {
        return where.joinToString(" AND ")
    }


    private fun select(where: String?, columns: String): String {
        return "SELECT $columns FROM ${tableName.toUpperCase()}${if (where == null) "" else " WHERE $where"};"
    }

    fun connection() = connection

}