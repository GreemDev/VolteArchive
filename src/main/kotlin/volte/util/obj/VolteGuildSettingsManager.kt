package volte.util.obj

import com.jagrosh.jdautilities.command.GuildSettingsManager
import net.dv8tion.jda.api.entities.Guild
import volte.database.entities.GuildData
import volte.meta.*

class VolteGuildSettingsManager : GuildSettingsManager<GuildData> {
    override fun getSettings(guild: Guild): GuildData {
        return guild.getData()
    }
}