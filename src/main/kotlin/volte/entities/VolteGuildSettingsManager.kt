package volte.entities

import com.jagrosh.jdautilities.command.GuildSettingsManager
import com.jagrosh.jdautilities.command.GuildSettingsProvider
import net.dv8tion.jda.api.entities.Guild
import volte.Volte
import volte.database.GuildData
import volte.database.VolteDatabase

class VolteGuildSettingsManager : GuildSettingsManager<VolteGuildSettings> {
    override fun getSettings(guild: Guild): VolteGuildSettings {
        val db = VolteDatabase.createNew()
        val rs = db.getRecordsFor(guild.id)
        return VolteGuildSettings(GuildData(rs))
    }
}

class VolteGuildSettings(private val data: GuildData) : GuildSettingsProvider {

    fun data() = data

    override fun getPrefixes(): MutableCollection<String> = mutableListOf(data.prefix())
}