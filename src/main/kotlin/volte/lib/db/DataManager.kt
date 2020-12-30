package volte.lib.db

import java.sql.ResultSet

open class DataManager(private val connector: DatabaseConnector, private val tableName: String) {

    protected fun read(query: String, func: (ResultSet) -> Unit) {
        val statement = connection().createStatement()
        val rs = statement.executeQuery(query)

        func(rs)
    }


    protected fun <T> read(query: String, func: (ResultSet) -> T): T {
        val statement = connection().createStatement()
        val rs = statement.executeQuery(query)

        return func(rs)
    }

    protected fun readWrite(query: String): ResultSet {
        return readWrite<ResultSet>(query) { rs -> rs }
    }

    protected fun read(query: String): ResultSet {
        return read<ResultSet>(query) { rs -> rs }
    }

    protected fun readWrite(query: String, func: (ResultSet) -> Unit) {
        val statement = connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
        val rs = statement.executeQuery(query)

        func(rs)
    }

    protected fun <T> readWrite(query: String, func: (ResultSet) -> T): T {
        val statement = connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
        val rs = statement.executeQuery(query)

        return func(rs)
    }

    protected fun select(where: String, vararg columns: SQLColumn<*>): String {
        val selection = StringBuilder()
        for (col in columns) {
            selection.append("${col.name()}, ")
        }

        return select(where, selection.toString().trim { it == ' ' || it == ',' })
    }

    protected fun selectAll(): String = select(null, "*")

    protected fun selectAll(where: String): String = select(where, "*")



    private fun select(where: String?, columns: String): String {
        return "SELECT $columns FROM ${tableName.toUpperCase()} ${if (where == null) "" else " WHERE $where"}"
    }

    fun connection() = connector.connection()

}