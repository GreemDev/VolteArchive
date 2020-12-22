package volte.database.api.columns

import volte.database.api.SQLColumn
import java.sql.ResultSet
import java.time.Instant

class InstantColumn(
    private val name: String,
    nullable: Boolean,
    private val default: Instant = Instant.MIN,
    private val primaryKey: Boolean = false
) : SQLColumn<Instant>(name, nullable, default, primaryKey) {
    override fun dataDescription(): String
        = "BIGINT DEFAULT ${default.toEpochMilli()} ${nullableStr()} ${if (primaryKey) " PRIMARY KEY" else ""}"

    override fun getValue(rs: ResultSet): Instant {
        rs.getLong(name).also {
            return if (it != 0L) Instant.ofEpochMilli(it) else default
        }
    }

    override fun updateValue(rs: ResultSet, new: Instant) = rs.updateLong(name, new.toEpochMilli())

}