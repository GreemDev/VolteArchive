package volte.commands.operator

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.commands.parsers.RoleParser
import volte.database.VolteDatabase
import volte.meta.Constants
import volte.meta.createEmbed

class OperatorCommand : Command() {

    init {
        this.name = "operator"
        this.aliases = arrayOf("op", "setop", "oprole")
        this.help = "Sets the role to be authorized to use Volte's Moderation and Admin commands."
        this.category = Constants.operatorCategory()
    }

    override fun execute(event: CommandEvent) {

        val db = VolteDatabase.createNew()

        if (event.args.isEmpty()) {
            val op = db.getStringFor(event.guild.id, "operator")
            if (op.isEmpty()) {
                event.reply(event.createEmbed("The operator role currently isn't set. Please run this command again with a role ID to set it."))
            } else {
                event.reply(event.createEmbed("The current Operator role is <@&${op}>"))
            }
            return
        }

        val role = RoleParser().parse(event, event.args)
        if (role == null) {
            event.reply(event.createEmbed("You didn't provide a valid role to be set. I can accept @mentions, IDs, or just names."))
            return
        }

        val statement = db.currentConnection().prepareStatement("UPDATE guilds SET operator = ? WHERE id = ${event.guild.id}")
        statement.setString(1, event.args)
        statement.executeUpdate()
        db.currentConnection().close()


        event.reply(event.createEmbed("Successfully set the Operator role to ${role.asMention}"))
    }

}