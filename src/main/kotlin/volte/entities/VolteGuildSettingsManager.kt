package volte.entities

import com.jagrosh.jdautilities.command.GuildSettingsManager
import com.jagrosh.jdautilities.command.GuildSettingsProvider
import net.dv8tion.jda.api.entities.Guild
import volte.Volte
import volte.database.GuildData
import volte.database.VolteDatabase

class VolteGuildSettingsManager : GuildSettingsManager<VolteGuildSettingsProviderImpl> {
    override fun getSettings(guild: Guild): VolteGuildSettingsProviderImpl {
        return VolteGuildSettingsProviderImpl(guild.id)
    }
}

class VolteGuildSettingsProviderImpl(val id: String) : GuildSettingsProvider {

    override fun getPrefixes(): MutableCollection<String> = mutableListOf(Volte.db().getAllSettingsFor(id).getPrefix())
}