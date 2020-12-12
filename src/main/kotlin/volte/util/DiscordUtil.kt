package volte.util

import org.apache.commons.lang3.StringUtils
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
                text.substring(3, text.length - 4); //<@!123>
            else
                text.substring(2, text.length - 3); //<@123>
        } else null
    }

    fun parseChannel(text: String): String? {
        return if (text.length >= 3 && text[0] == '<' && text[1] == '#' && text.endsWith('>')) {
            text.substring(2, text.length - 3) //<#123>
        } else null
    }

    fun parseDate(id: String) {
        if (StringUtils.isNumeric(id)) {
            val rawId = id.toLong()
            //val date = Date.from(Instant.)
        } else {
            throw IllegalArgumentException("id must be a number.")
        }
    }

}