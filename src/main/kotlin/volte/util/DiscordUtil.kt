package volte.util

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import org.apache.commons.lang3.StringUtils
import volte.Volte
import volte.meta.getData
import volte.meta.getHighestRoleWithColor
import volte.static
import java.awt.Color
import java.time.Instant
import java.util.*

object DiscordUtil {
    @static fun parseRole(text: String): String? {
        return if (text.length >= 4 && text[0] == '<' && text[1] == '@' && text[2] == '&' && text.endsWith('>')) {
            text.substring(3, text.length - 4) //<@&123>
        } else null
    }

    @static fun parseUser(text: String): String? {
        return if (text.length >= 3 && text[0] == '<' && text[1] == '@' && text.endsWith('>')) {
            return if (text.length >= 4 && text[2] == '!')
                text.substring(3, text.length - 4) //<@!123>
            else
                text.substring(2, text.length - 3) //<@123>
        } else null
    }

    @static fun createDefaultEmbed(content: String? = null, member: Member): EmbedBuilder = EmbedBuilder()
        .setColor(member.getHighestRoleWithColor()?.color ?: Color.GREEN)
        .setFooter("Requested by ${member.user.asTag}", member.user.effectiveAvatarUrl)
        .setDescription(content ?: "")

    @static fun parseChannel(text: String): String? {
        return if (text.length >= 3 && text[0] == '<' && text[1] == '#' && text.endsWith('>')) {
            text.substring(2, text.length - 3) //<#123>
        } else null
    }

    @static fun isBotOwner(member: Member): Boolean {
        return member.id == Volte.config().owner()
    }

    @static fun isBotOwner(event: CommandEvent) : Boolean {
        return isBotOwner(event.member)
    }

    @static fun isOperator(member: Member): Boolean {
        return (isBotOwner(member) or member.isOwner or (member.hasPermission(Permission.ADMINISTRATOR)) or member.roles.any { role ->
            role.id == member.guild.getData().getOperator()
        })
    }

    @static fun isOperator(event: CommandEvent): Boolean {
        return isOperator(event.member)
    }

    /**
     * Parses the given [id] as a Discord Snowflake and returns it as an [java.time.Instant].
     * If the parse fails, it throws an [IllegalArgumentException].
     */
    @static fun parseSnowflake(id: String): Instant {
        return if (StringUtils.isNumeric(id)) {
            Date((id.toLong().shr(22) + 1420070400000L)).toInstant()
        } else {
            throw IllegalArgumentException("id must be a number.")
        }
    }

    /**
     * Tries to parse the given [id] as a Discord Snowflake and returns it as an [Instant].
     * If the parse fails, it returns [Instant.MIN].
     */
    @static fun tryParseSnowflake(id: String): Instant {
        return try {
            parseSnowflake(id)
        } catch (e: IllegalArgumentException) {
            return Instant.MIN
        }
    }

    /**
     * Parses the [color] argument into a [Color].
     * The string must be in the format `r;g;b`.
     */
    @static fun parseColor(color: String): Color {

        val split = color.split(";").map(String::trim)
        val r = split[0].toInt()
        val g = split[1].toInt()
        val b = split[2].toInt()
        return Color(r, g, b)
    }

    /**
     * Returns a [PermissionsResult] containing the allowed and denied permissions of the given [member].
     */
    @static fun prettyPermissions(member: Member): PermissionsResult {
        val allowed = member.permissions.clone().toList()
        val denied = Permission.values().mapNotNull {
            if (allowed.contains(it))
                return@mapNotNull null
            else
                return@mapNotNull it
        }

        return PermissionsResult(allowed, denied)
    }

    class PermissionsResult(val allowed: List<Permission>, val denied: List<Permission>)
}