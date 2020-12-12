package volte.entities

import com.jagrosh.jdautilities.command.GuildSettingsManager
import com.jagrosh.jdautilities.command.GuildSettingsProvider
import net.dv8tion.jda.api.entities.Guild
import volte.Volte
import volte.database.GuildData
import volte.database.VolteDatabase

class VolteGuildSettingsManager : GuildSettingsManager<VolteGuildSettingsProviderImpl> {
    override fun getSettings(guild: Guild): VolteGuildSettingsProviderImpl {
        val db = VolteDatabase.createNew()
        return VolteGuildSettingsProviderImpl(GuildData(db.getRecordsFor(guild.id), db))
    }
}

class VolteGuildSettingsProviderImpl(private val data: GuildData) : GuildSettingsProvider {

    fun data() = data

    override fun getPrefixes(): MutableCollection<String> = mutableListOf(data.prefix())
}