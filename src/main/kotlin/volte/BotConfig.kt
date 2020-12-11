package volte

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.dv8tion.jda.api.entities.Activity
import org.apache.commons.io.FileUtils
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

    fun token() = token
    fun game() = game
    fun prefix() = commandPrefix
    fun owner() = ownerId

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
            FileUtils.write(file(), gson.toJson(BotConfig()), Charset.forName("UTF-8"))
        }

        fun get(): BotConfig? {
            return try {
                gson.fromJson(FileUtils.readFileToString(file(), Charset.forName("UTF-8")), BotConfig::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }

    fun parseActivity(): Activity {
        val activity = this.game.replace(this.game.split(" ").first(), "").trim()
        return when (this.game.toLowerCase().split(" ").first()) {
            "playing" -> Activity.playing(activity)
            "listeningto" -> Activity.listening(activity)
            "watching" -> Activity.watching(activity)
            else -> Activity.playing(activity)
        }
    }
}
