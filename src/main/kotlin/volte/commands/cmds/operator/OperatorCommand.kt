package volte.commands.cmds.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.Role
import volte.Volte
import volte.commands.parsers.Parsers
import volte.meta.categories.operator
import volte.meta.createEmbed

class OperatorCommand : Command() {

    init {
        this.name = "operator"
        this.aliases = arrayOf("op", "setop", "oprole")
        this.help = "Sets the role to be authorized to use Volte's Moderation and Admin commands."
        this.guildOnly = true
        this.category = operator()
    }

    override fun execute(event: CommandEvent) {
        val data = Volte.db().getSettingsFor(event.guild.id)

        if (event.args.isEmpty()) {
            if (data.getOperator().isEmpty()) {
                event.message.reply(event.createEmbed("The operator role currently isn't set. Please run this command again with a role ID to set it."))
                    .queue()
            } else {
                event.message.reply(event.createEmbed("The current Operator role is <@&${data.getOperator()}>")).queue()
            }
            return
        }

        val role = Parsers.parse<Role>(event, event.args)
        if (role == null) {

            event.message.reply(event.createEmbed("You didn't provide a valid role to be set. I can accept @mentions, IDs, or just names."))
                .queue()
        } else {
            data.setOperator(role.id)

            event.message.reply(event.createEmbed("Successfully set the Operator role to ${role.asMention}")).queue()
        }

    }

}