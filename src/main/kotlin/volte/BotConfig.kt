package volte

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.dv8tion.jda.api.entities.Activity
import volte.meta.optional
import volte.meta.entities.Optional
import volte.meta.fromJson
import java.io.File
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
            if (!file().exists()) {
                this.write()
                Volte.logger().warn("Please fill in the config located at data/config.json, and restart me!")
                exitProcess(-1)
            }
        }

        private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        fun file() = File("data/volte.json")

        fun write(config: BotConfig = BotConfig()) {
            file().writeText(gson.toJson(config))
        }

        fun get(): Optional<BotConfig> {
            return Optional of try {
                gson.fromJson<BotConfig>(file().readText())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }

    fun parseActivity(): Activity =
        with(this.game.replace(this.game.split(" ").first(), "").trim()) {
            when (game.toLowerCase().split(" ").first()) {
                "playing" -> Activity.playing(this)
                "listening" -> Activity.listening(this)
                "listeningto" -> Activity.listening(this)
                "watching" -> Activity.watching(this)
                "competing" -> Activity.competing(this)
                "competingin" -> Activity.competing(this)
                else -> Activity.playing(this)
            }
        }
}
