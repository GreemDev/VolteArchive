package volte.meta

import com.jagrosh.jdautilities.command.Command.Category
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Role
import volte.Volte
import volte.database.GuildData
import volte.database.VolteDatabase
import java.awt.Color

object Constants {

    fun ownerCategory(): Category = Category("Owner") { event ->
        Volte.config().owner() == event.author.id
    }

    fun operatorCategory(): Category = Category("Operator") { event ->
        val db = VolteDatabase.createNew()
        val data = db.getAllSettingsFor(event.guild.id)
        (data.resultSet().next() and (event.member.roles.any { role ->
            role.id == data.operator()
        }
                or (event.member.hasPermission(Permission.ADMINISTRATOR))
                or event.member.isOwner))
            .also {
                db.closeConnection()
            }
    }

}

fun stopwatch (func: () -> Unit): Long {
    var end: Long
    val start: Long = System.currentTimeMillis().also {
        func().also {
            end = System.currentTimeMillis()
        }
    }


    return end - start
}

fun CommandEvent.reply(content: String, func: EmbedBuilder.() -> Unit) {
    val e = createEmbedBuilder(content)
    func(e)
    reply(e.build())
}

fun CommandEvent.reply(func: EmbedBuilder.() -> Unit) {
    val e = createEmbedBuilder()
    func(e)
    reply(e.build())
}

fun CommandEvent.createEmbed(content: String): MessageEmbed = this.createEmbedBuilder(content).build()

fun CommandEvent.createEmbedBuilder(content: String? = null): EmbedBuilder = EmbedBuilder()
    .setColor(member.getHighestRoleWithColor()?.color ?: Color.GREEN)
    .setFooter("Requested by ${author.asTag}", author.effectiveAvatarUrl)
    .setDescription(content ?: "")


fun Member.getHighestRoleWithColor(): Role? = this.roles.firstOrNull { it.color != null }

fun Guild.getData(): GuildData {
    val db = VolteDatabase.createNew()
    return db.getAllSettingsFor(id)

}
