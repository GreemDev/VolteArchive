package volte

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import volte.commands.cmds.operator.OperatorCommand
import volte.commands.cmds.owner.EvalCommand
import volte.commands.cmds.utilities.InfoCommand
import volte.commands.cmds.utilities.PingCommand
import volte.database.VolteDatabase
import volte.entities.CommandHandler
import volte.entities.VolteGuildSettingsManager
import volte.meta.Emoji
import volte.meta.stopwatch
import volte.modules.AntilinkModule
import volte.modules.AutoroleModule
import volte.modules.MassPingModule
import javax.security.auth.login.LoginException

class Volte private constructor() {

    fun jda() = jda
    fun commands() = commandClient
    fun config() = BotConfig.get()!!


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Volte::class.java)
        fun logger() = this.logger

        private lateinit var jda: JDA
        private lateinit var commandClient: CommandClient
        private lateinit var database: VolteDatabase

        fun jda() = jda
        fun commands() = commandClient
        fun db() = database
        fun config() = BotConfig.get()!!

        fun start() {
            Volte()
        }

    }

    init {
        BotConfig.checks()

        val elapsed = stopwatch {
            Runtime.getRuntime().addShutdownHook(Thread {
                db().connector().shutdown()
                jda().cancelRequests()
                jda().shutdownNow()
            })

            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                logger.error(throwable.message, throwable.cause)
            }

            RestAction.setPassContext(true)

            try {
                commandClient = CommandClientBuilder()
                    .setOwnerId(config().owner())
                    .setPrefix(config().prefix())
                    .setHelpWord("help")
                    .addCommands(OperatorCommand(), InfoCommand(), PingCommand(), EvalCommand())
                    .setGuildSettingsManager(VolteGuildSettingsManager())
                    .setListener(CommandHandler())
                    .setServerInvite("https://greemdev.net/Discord")
                    .setEmojis(Emoji.BALLOT_BOX_WITH_CHECK, Emoji.WARNING, Emoji.X)
                    .setShutdownAutomatically(true)
                    .build()

                jda = JDABuilder.createDefault(config().token())
                    .addEventListeners(commandClient)
                    .disableCache(CacheFlag.EMOTE, CacheFlag.ACTIVITY)
                    .build().awaitReady()
            } catch (e: LoginException) {
                logger.error("Failed to login to Discord: ${e.message}")
            } catch (e: InterruptedException) {
                logger.error("Failed to login to Discord: ${e.message}")
            }

            database = VolteDatabase().apply {
                initializeDb()
            }

            for (klass in arrayListOf(
                AutoroleModule::class,
                AntilinkModule::class,
                MassPingModule::class
            )) {
                logger.info("Adding module ${klass.java.simpleName.replace("Module", "")}...")
                val module: EventListener = klass.java.constructors[0].newInstance() as EventListener

                jda.addEventListener(module)
            }

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

        logger.info("Initialization finished in ${elapsed}ms. Volte v4.0.0 is ready.")
        logger.info("Available commands: [${commandClient.commands.joinToString(", ") { c -> c.name.capitalize() }}]")


    }

}