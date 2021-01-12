package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Role
import volte.Volte
import volte.commands.parsers.Parsers
import volte.meta.categories.operator
import volte.meta.createEmbed

class AutoroleCommand : Command() {

    init {
        this.name = "autorole"
        this.help = "Shows or sets the autorole for the current guild."
        this.guildOnly = true
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        val data = Volte.db().getSettingsFor(event.guild.id)
        val autorole = data.getAutorole()

        if (event.args.isEmpty()) {
            if (autorole.isEmpty()) {
                event.message.reply(event.createEmbed("Autorole isn't currently set. Please run this command again with a role ID to set it."))
                    .queue()
            } else {
                event.message.reply(event.createEmbed("The current Autorole is <@&${autorole}>")).queue()
            }
            return
        }

        val role = Parsers.parse<Role>(event, event.args)
        if (role == null) {
            event.message.reply(event.createEmbed("You didn't provide a valid role to be set. I can accept @mentions, IDs, or just names."))
                .queue()
        } else {
            data.setAutorole(role.id)

            event.message.reply(event.createEmbed("Successfully set the role to be given to members on join to ${role.asMention}"))
                .queue()
        }
    }
}