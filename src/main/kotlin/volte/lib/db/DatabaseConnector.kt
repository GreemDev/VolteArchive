package volte.lib.db

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnector(location: String) {

    private val connection: Connection
    fun connection() = connection

    private infix fun logger(func: Logger.() -> Unit) {
        LoggerFactory.getLogger("Database").apply(func)
    }


    init {
        try {
            connection = DriverManager.getConnection("jdbc:h2:$location")
            logger {
                info(
                    "Using ${connection.metaData.driverName}, connected to ${connection.metaData.url}"
                )
            }
        } catch (e: SQLException) {
            throw e
        }
    }

    fun shutdown() {
        try {
            connection.close()
        } catch (e: SQLException) {
            logger {
                error("Failed to close the database connection: ", e)
            }
        }
    }

}