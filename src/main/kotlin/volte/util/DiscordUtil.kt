package volte.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import org.apache.commons.lang3.StringUtils
import volte.meta.getData
import volte.meta.getHighestRoleWithColor
import java.awt.Color
import java.time.Instant
import java.util.*

object DiscordUtil {

    fun parseRole(text: String): String? {
        return if (text.length >= 4 && text[0] == '<' && text[1] == '@' && text[2] == '&' && text.endsWith('>')) {
            text.substring(3, text.length - 4) //<@&123>
        } else null
    }

    fun parseUser(text: String): String? {
        return if (text.length >= 3 && text[0] == '<' && text[1] == '@' && text.endsWith('>')) {
            return if (text.length >= 4 && text[2] == '!')
                text.substring(3, text.length - 4) //<@!123>
            else
                text.substring(2, text.length - 3) //<@123>
        } else null
    }

    fun createDefaultEmbed(content: String? = null, member: Member): EmbedBuilder = EmbedBuilder()
        .setColor(member.getHighestRoleWithColor()?.color ?: Color.GREEN)
        .setFooter("Requested by ${member.user.asTag}", member.user.effectiveAvatarUrl)
        .setDescription(content ?: "")

    fun parseChannel(text: String): String? {
        return if (text.length >= 3 && text[0] == '<' && text[1] == '#' && text.endsWith('>')) {
            text.substring(2, text.length - 3) //<#123>
        } else null
    }

    fun isOperator(member: Member): Boolean {
        return (member.isOwner or (member.hasPermission(Permission.ADMINISTRATOR)) or member.roles.any { role ->
            role.id == member.guild.getData().getOperator()
        })
    }

    fun parseSnowflake(id: String): Instant {
        return if (StringUtils.isNumeric(id)) {
            Date((id.toLong().shl(22) + 1420070400000)).toInstant()
        } else {
            throw IllegalArgumentException("id must be a number.")
        }
    }

    fun parseColor(color: String): Color {
        val split = color.split(";")
        val r = split[0].toFloat()
        val g = split[1].toFloat()
        val b = split[2].toFloat()
        return Color(r, g, b)
    }

}