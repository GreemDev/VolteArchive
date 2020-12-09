package volte

import net.dv8tion.jda.api.JDA
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import volte.commands.operator.OperatorCommand
import volte.commands.owner.SqlCommand
import volte.database.VolteDatabase

class Volte private constructor() {

    fun jda() = jda
    fun commands() = commandClient
    fun config() = BotConfig.get()!!


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Volte::class.java)
        fun logger() = this.logger

        private lateinit var jda: JDA
        private lateinit var commandClient: CommandClient

        fun jda() = jda
        fun commands() = commandClient
        fun config() = BotConfig.get()!!

        fun start() {
            Volte()
        }

    }

    init {
        BotConfig.checks()

        commandClient = CommandClientBuilder()
            .setOwnerId(config().owner())
            .setPrefix(config().prefix())
            .setHelpWord("help")
            .addCommands(OperatorCommand(), SqlCommand())
            .build()

        jda = JDABuilder.createDefault(config().token())
            .addEventListeners(commandClient)
            .disableCache(CacheFlag.EMOTE, CacheFlag.ACTIVITY)
            .build().awaitReady();

        VolteDatabase.createNew().initializeDb(this)

        val activity = config().parseActivity()
        jda.presence.setPresence(OnlineStatus.ONLINE, activity)

            if (config().game().contains(" ")) {
                val conts = config().game().toLowerCase().split(" ")
                if (arrayListOf("playing", "watching", "listeningto").none {
                        conts.first() == it
                    }
                ) {
                    logger.warn(
                        "Your game wasn't set properly. " +
                                "You entered the activity as ${conts.first()}" +
                                "instead of a valid activity: Playing, Listeningto, or Watching."
                    )
                    logger.warn("Your bot's game has been set to \"Playing ${activity.name}\"")
                } else {
                    val activityType = activity.type.toString().toLowerCase().capitalize()
                    logger.info("Set the activity to \"${if (activityType == "Default") "Playing" else activityType} ${activity.name}\"")
                }
            }

    }

}