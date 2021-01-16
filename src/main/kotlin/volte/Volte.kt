package volte

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import okhttp3.OkHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import volte.database.VolteDatabase
import volte.lib.kjda.*
import volte.lib.meta.*
import volte.modules.*
import volte.lib.meta.entities.*
import java.time.Duration
import java.util.*
import kotlin.reflect.KClass
import kotlin.system.measureTimeMillis


class Volte private constructor() {
    companion object {
        infix fun logger(jclass: Class<*>): Logger = LoggerFactory.getLogger(jclass)
        infix fun logger(klass: KClass<*>): Logger = logger(klass.java)
        fun logger() = logger(Volte::class)
        infix fun logger(func: Logger.() -> Unit) = logger().apply(func)
        fun logger(klass: KClass<*>, func: Logger.() -> Unit) = with(logger(klass), func)


        private lateinit var shardedJda: ShardManager
        private lateinit var commandClient: CommandClient
        private lateinit var database: VolteDatabase
        private var http = OkHttpClient.Builder().apply {
            callTimeout(Duration.ofSeconds(10))
            readTimeout(Duration.ofSeconds(5))
            writeTimeout(Duration.ofSeconds(5))
        }.build()

        private var isStarted: Boolean = false

        fun jda() = shardedJda
        fun commands() = commandClient
        fun db() = database
        fun config() = BotConfig.get().value()

        fun start() {
            if (!isStarted) {
                isStarted = true
                Volte()
            } else {
                throw IllegalStateException("Volte is already started; cannot start it again.")
            }
        }

    }

    init {
        BotConfig.checks()

        val elapsed = measureTimeMillis {
            Runtime.getRuntime().addShutdownHook(Thread {
                db().connection().close()
                jda().shardCache.forEach(JDA::cancelRequests)
                jda().shardCache.forEach(JDA::shutdownNow)
            })

            MessageAction.setDefaultMentionRepliedUser(false)
            MessageAction.setDefaultMentions(EnumSet.complementOf(EnumSet.of(MentionType.EVERYONE, MentionType.HERE)))

            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                logger {
                    error("Thread \"${thread.name}\" terminated from \"${throwable.message}\"")
                }

                GuildLoggingManager.instance.onError(throwable)
                throwable.printStackTrace()
            }

            RestAction.setPassContext(true)

            commandClient = CommandClientBuilder()
                .setOwnerId(config().owner())
                .setPrefix(config().prefix())
                .setHelpWord("help")
                .withVolteCommands()
                .setGuildSettingsManager { guild -> guild.getData() }
                .setListener(CommandHandler())
                .setServerInvite("https://greemdev.net/Discord")
                .setEmojis(Emoji.BALLOT_BOX_WITH_CHECK, Emoji.WARNING, Emoji.X)
                .setShutdownAutomatically(true)
                .build()

            try {
                shardedJda = client(config().token()) {
                    setMemberCachePolicy(MemberCachePolicy.ALL)
                    disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE)
                    enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                    setHttpClient(http)

                    if (Version.releaseType == Version.ReleaseType.DEVELOPMENT) {
                        this += DebugLogger()
                        setRawEventsEnabled(true)
                    }
                    this += commandClient
                    this += GuildLoggingManager.instance
                }

            } catch (e: Exception) {
                logger().error("Failed to login to Discord: ${e.message}")
            }

            database = VolteDatabase()
            database.initializeDb()

            for (klass in arrayListOf(
                AutoroleModule::class,
                AntilinkModule::class,
                MassPingModule::class,
                WelcomeModule::class,
                QuoteModule::class,
                DatabaseSynchronizer::class,
            )) {
                logger().info("Adding module ${klass.java.simpleName.replace("Module", "")}...")
                val module = klass.java.constructors[0].newInstance()

                shardedJda += module
            }

            val activity = config().parseActivity()
            shardedJda.setPresence(OnlineStatus.DO_NOT_DISTURB, activity)

            if (config().game().contains(" ")) {
                val conts = config().game().toLowerCase().split(" ")
                if (arrayListOf("playing", "watching", "listening", "listeningto", "competing", "competingin").none(
                        conts.first()::equals
                    )
                ) {
                    logger {
                        warn(
                            "Your game wasn't set properly. " +
                                    "You entered the activity as ${conts.first()}" +
                                    "instead of a valid activity: Playing, Listening[to], Competing[in], or Watching."
                        )
                        warn("Your bot's game has been set to \"Playing ${activity.name}\"")
                    }
                } else {
                    val activityType = activity.type.name.toLowerCase().capitalize()
                    logger().info("Set the activity to \"${if (activityType == "Default") "Playing" else activityType} ${activity.name}\"")
                }
            }
        }

        val cont = "${shardedJda.shardsTotal} shard" + if (shardedJda.shardsTotal == 1) "" else "s"
        logger {
            info("Initialization of $cont finished in ${elapsed}ms. Volte v${Version.formatted()} is ready.")
            info("Available commands: ${commands().commands.size.inc()}") //incremented for the help command
        }
    }

}