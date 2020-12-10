package volte.meta

import com.jagrosh.jdautilities.command.Command.Category
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Role
import volte.Volte
import volte.database.VolteDatabase
import java.awt.Color

object Constants {

    fun ownerCategory(): Category = Category("Owner") { event ->
        Volte.config().owner() == event.author.id
    }

    fun operatorCategory(): Category = Category("Operator") { event ->
        val db = VolteDatabase.createNew()
        val rs = db.getRecordsFor(event.guild.id)
        (rs.next() and (event.member.roles.any { role ->
            role.id == rs.getString("operator")
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

fun CommandEvent.createEmbed(content: String): MessageEmbed = this.createEmbedBuilder(content).build()

fun CommandEvent.createEmbedBuilder(content: String? = null): EmbedBuilder = EmbedBuilder()
    .setColor(member.getHighestRoleWithColor()?.color ?: Color.GREEN)
    .setFooter("Requested by ${author.asTag}", author.effectiveAvatarUrl)
    .setDescription(content ?: "")


fun Member.getHighestRoleWithColor(): Role? = this.roles.firstOrNull { it.color != null }
