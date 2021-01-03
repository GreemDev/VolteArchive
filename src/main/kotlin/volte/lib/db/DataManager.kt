package volte.lib.db

import java.sql.ResultSet

open class DataManager(private val connector: DatabaseConnector, var tableName: String) {

    init {
        tableName = tableName.toUpperCase()
    }

    open fun allColumns(): List<SQLColumn<*>> {
        return emptyList()
    }

    internal fun query(query: String, func: (ResultSet) -> Unit) {
        val statement = connection().createStatement()
        val rs = statement.executeQuery(query)

        func(rs)
    }

    internal fun <T> query(query: String, func: (ResultSet) -> T): T {
        val statement = connection().createStatement()
        val rs = statement.executeQuery(query)

        return func(rs)
    }

    internal fun queryMutable(query: String): ResultSet {
        return queryMutable<ResultSet>(query) { rs -> rs }
    }

    internal fun query(query: String): ResultSet {
        return query<ResultSet>(query) { rs -> rs }
    }

    internal fun queryMutable(query: String, func: (ResultSet) -> Unit) {
        val statement = connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
        val rs = statement.executeQuery(query)

        func(rs)
    }

    internal fun <T> queryMutable(query: String, func: (ResultSet) -> T): T {
        val statement = connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
        val rs = statement.executeQuery(query)

        return func(rs)
    }

    /**
     * Creates a SELECT statement with the given [where] and with the provided [SQLColumn]s.
     * If no [SQLColumn]s are provided, this returns a SELECT * statement.
     */
    internal fun select(where: String, vararg columns: SQLColumn<*> = arrayOf()): String = when {
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


    private fun select(where: String?, columns: String): String {
        return "SELECT $columns FROM ${tableName.toUpperCase()} ${if (where == null) "" else " WHERE $where"}"
    }

    fun connection() = connector.connection()

}