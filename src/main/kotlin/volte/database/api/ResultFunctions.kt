package volte.database.api

import java.sql.ResultSet
import java.sql.SQLException
import kotlin.jvm.Throws

@FunctionalInterface
interface ResultConsumer {

    @Throws(SQLException::class)
    fun consume(rs: ResultSet)
}

interface ResultFunction<T> {

    @Throws(SQLException::class)
    fun apply(rs: ResultSet): T
}