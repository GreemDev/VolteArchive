package volte

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.dv8tion.jda.api.entities.Activity
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

class BotConfig {

    private val token = "your-token-here"
    private val game = "your-game-here"
    private val commandPrefix = "your-prefix-here"
    private val ownerId = "your-id-here"
    private val firstStart = true

    fun token() = token
    fun game() = game
    fun prefix() = commandPrefix
    fun owner() = ownerId
    fun isFirstStart() = firstStart

    companion object {

        fun checks() {
            if (file().exists().not()) {
                this.write()
                Volte.logger().warn("Please fill in the config located at data/config.json, and restart me!")
                exitProcess(0)
            }
        }

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        private const val configLoc = "data/volte.json"
        fun file() = File(configLoc)

        fun write() {
            Files.createFile(file().toPath())
            Files.writeString(file().toPath(), gson.toJson(BotConfig()))
        }

        fun get(): BotConfig? {
            return try {
                gson.fromJson(Files.readString(Path.of(configLoc), Charset.defaultCharset()), BotConfig::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }

    fun parseActivity(): Activity {
        val lowercaseArray = this.game.toLowerCase().split(" ")
        val activity = this.game.replace(this.game.split(" ").first(), "").trim { it <= ' ' }
        return when (lowercaseArray.first()) {
            "playing" -> Activity.playing(activity)
            "listeningto" -> Activity.listening(activity)
            "watching" -> Activity.watching(activity)
            else -> Activity.playing(activity)
        }
    }
}
