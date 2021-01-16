package volte.lib.db

import java.sql.ResultSet

/**
 * This method is used to run SQL in a single method without having read/write methods.
 *
 *
 * If [isQuery] is true, the [ResultSet] return value will never be null because the statement is being run as a query, AKA, a read operation.
 *
 * If [isQuery] is false, the [ResultSet] return value will always be null because the statement is being run as an update, AKA, a write operation.
 */
fun DbManager.sql(type: StatementType, rawStatement: () -> String): ResultSet? {
    val statement = this.connection().prepareStatement(rawStatement())
    return when (type) {
        StatementType.QUERY -> {
            statement.executeQuery()
        }
        StatementType.UPDATE -> {
            statement.executeUpdate()
            null
        }
    }
}

enum class StatementType {
    QUERY,
    UPDATE;

    override fun toString(): String {
        return this.name.toLowerCase().capitalize()
    }
}