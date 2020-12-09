package volte.util

object MentionUtil {

    fun parseRole(text: String): String? {
        return if (text.length >= 4 && text[0] == '<' && text[1] == '@' && text[2] == '&' && text[text.length.dec()] == '>') {
            text.substring(3, text.length - 4)
        } else null
    }

}