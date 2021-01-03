package volte

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.dv8tion.jda.api.entities.Activity
import org.apache.commons.io.FileUtils
import volte.meta.optional
import volte.util.obj.Optional
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

class BotConfig {

    private val token = "your-token-here"
    private val game = "your-game-here"
    private val commandPrefix = "your-prefix-here"
    private val ownerId = "your-id-here"
    private val guildLogging: GuildLogging = GuildLogging()

    fun token() = token
    fun game() = game
    fun prefix() = commandPrefix
    fun owner() = ownerId
    fun guildLogging() = guildLogging

    class GuildLogging {
        private val channelId: String = "your-logging-channel-here"
        private val enabled: Boolean = false

        fun channel(): String = channelId
        fun enabled(): Boolean = enabled
    }


    companion object {

        fun checks() {
            if (file().exists().not()) {
                this.write()
                Volte.logger().warn("Please fill in the config located at data/config.json, and restart me!")
                exitProcess(0)
            }
        }

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        fun file() = File("data/volte.json")

        fun write() {
            FileUtils.write(file(), gson.toJson(BotConfig()), Charset.forName("UTF-8"))
        }

        fun get(): Optional<BotConfig> {
            return try {
                gson.fromJson(FileUtils.readFileToString(file(), Charset.forName("UTF-8")), BotConfig::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }.optional()
        }

    }

    fun parseActivity(): Activity {
        val activity = this.game.replace(this.game.split(" ").first(), "").trim()
        val type = this.game.toLowerCase().split(" ").first()
        return when (type) {
            "playing" -> Activity.playing(activity)
            "listening" -> Activity.listening(activity)
            "listeningto" -> Activity.listening(activity)
            "watching" -> Activity.watching(activity)
            "competing" -> Activity.competing(activity)
            "competingin" -> Activity.competing(activity)
            else -> Activity.playing(activity)
        }
    }
}
