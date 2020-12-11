package volte

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.GuildSettingsManager
import com.jagrosh.jdautilities.command.GuildSettingsProvider
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import volte.commands.cmds.operator.OperatorCommand
import volte.commands.cmds.utilities.InfoCommand
import volte.commands.cmds.utilities.PingCommand
import volte.database.VolteDatabase
import volte.entities.VolteGuildSettingsManager
import volte.meta.Emoji

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
            .addCommands(OperatorCommand(), InfoCommand(), PingCommand())
            .setGuildSettingsManager(VolteGuildSettingsManager())
            .setServerInvite("https://greemdev.net/Discord")
            .setEmojis(Emoji.BALLOT_BOX_WITH_CHECK, Emoji.WARNING, Emoji.X)
            .setShutdownAutomatically(true)
            .build()

        jda = JDABuilder.createDefault(config().token())
            .addEventListeners(commandClient)
            .disableCache(CacheFlag.EMOTE, CacheFlag.ACTIVITY)
            .build().awaitReady()

        VolteDatabase.createNew().initializeDb()

        val activity = config().parseActivity()
        jda.presence.setPresence(OnlineStatus.ONLINE, activity)

        if (config().game().contains(" ")) {
            val conts = config().game().toLowerCase().split(" ")
            if (arrayListOf("playing", "watching", "listeningto").none { conts.first() == it }) {
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