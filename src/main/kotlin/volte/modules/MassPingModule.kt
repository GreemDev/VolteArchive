package volte.modules

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import volte.meta.getData

class MassPingModule : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.guild.getData().getMassPings().not()) return

        if (event.message.mentionsEveryone()) {
            event.message.delete().reason("Autodelete from Volte's mass ping checks because the message contained @everyone or @here.").queue()
        }
        if (event.message.getMentions().size > 10) {
            event.message.delete().reason("Autodelete from Volte's mass ping checks because the message contained more than 10 individual mentions.").queue()
        }
    }

}