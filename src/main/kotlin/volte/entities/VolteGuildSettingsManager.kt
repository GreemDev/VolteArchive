package volte.entities

import com.jagrosh.jdautilities.command.GuildSettingsManager
import com.jagrosh.jdautilities.command.GuildSettingsProvider
import net.dv8tion.jda.api.entities.Guild
import volte.Volte
import volte.database.entities.GuildData

class VolteGuildSettingsManager : GuildSettingsManager<GuildData> {
    override fun getSettings(guild: Guild): GuildData {
        return GuildData(guild.id, Volte.db())
    }
}