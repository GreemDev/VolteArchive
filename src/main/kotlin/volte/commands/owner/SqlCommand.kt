package volte.commands.owner

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import volte.BotConfig
import volte.database.VolteDatabase
import volte.meta.Constants
import volte.meta.createEmbed
import volte.meta.createEmbedBuilder

class SqlCommand : Command() {

    init {
        this.name = "sql"
        this.help = "Runs SQL code on Volte's database. WARNING: Extremely dangerous. Do not use if you don't know any SQL."
        this.category = Constants.ownerCategory()
    }

    override fun execute(event: CommandEvent) {
        val split = event.args.split(" ")
        if (split.isEmpty()) {
            event.reply(event.createEmbed("Please provide a type, and some SQL! Example: `\$sql query SELECT * FROM guilds`"))
            return
        }

        val type = split[0].toLowerCase()

        val db = VolteDatabase.createNew()
        val sql = split.subList(1, split.size).joinToString(" ")
        when (type) {
            "query" -> {
                val statement = db.currentConnection().prepareStatement(sql)
                val rs = statement.executeQuery()
                event.reply(event.createEmbed("```sql\n${BotConfig.gson.toJson(rs)}```"))
            }
            "command" -> {

            }
            else -> {
                event.reply(event.createEmbed("Invalid statement type. Valid types are `query` and `command`."))
            }
        }
    }
}