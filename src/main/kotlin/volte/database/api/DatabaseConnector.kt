package volte.database.api

import volte.Volte
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnector(location: String) {

    private val connection: Connection = DriverManager.getConnection("jdbc:h2:$location")
    fun connection() = connection


    init {
        Volte.logger().info("Connected to the database!")
    }

    fun shutdown() {
        try {
            connection.close()
        } catch (e: SQLException) {
            Volte.logger().error("Failed to close the database connection: ", e)
        }
    }

}