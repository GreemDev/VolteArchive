package volte.meta

import com.jagrosh.jdautilities.command.Command
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

    fun ownerCategory(): Command.Category = Command.Category("Owner") {
        Volte.config().owner() == it.author.id
    }

    fun operatorCategory(): Command.Category = Command.Category("Operator") {
        val db = VolteDatabase.createNew()
        val rs = db.createStatement().executeQuery("SELECT * FROM guilds WHERE id = ${it.guild.id}")
        (rs.next() and (it.member.roles.any { role ->
            role.id == rs.getString("operator")
        } or (it.member.hasPermission(Permission.ADMINISTRATOR)) or it.member.isOwner)).also {
            db.currentConnection().close()
        }
    }

}

fun CommandEvent.createEmbed(content: String): MessageEmbed {
    return this.createEmbedBuilder(content).build()
}

fun CommandEvent.createEmbedBuilder(content: String? = null): EmbedBuilder {
    return EmbedBuilder()
        .setColor(member.getHighestRoleWithColor()?.color ?: Color.GREEN)
        .setFooter("Requested by ${author.asTag}", author.effectiveAvatarUrl)
        .setDescription(content ?: "")
}

fun Member.getHighestRoleWithColor(): Role? {
    return this.roles.firstOrNull {
        it.color != null
    }
}