package volte.meta

import volte.database.api.SQLColumn
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.Command.Category
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.requests.RestAction
import volte.Volte
import volte.commands.cmds.operator.*
import volte.commands.cmds.owner.*
import volte.commands.cmds.utilities.*
import volte.database.entities.*
import volte.util.obj.RestPromise
import volte.util.DiscordUtil
import java.sql.ResultSet
import java.time.Instant
import kotlin.reflect.KClass

object Constants {
    fun ownerCategory(): Category = owner
    fun operatorCategory(): Category = operator
    fun utilityCategory(): Category = utility

    private val owner = Category("Owner", DiscordUtil::isBotOwner)
    private val operator = Category("Operator", DiscordUtil::isOperator)
    private val utility = Category("Utility")
}

inline fun CommandEvent.reply(content: String, func: EmbedBuilder.() -> Unit) {
    val e = createEmbedBuilder(content)
    func(e)
    reply(e.build())
}

inline fun CommandEvent.reply(func: EmbedBuilder.() -> Unit) = reply(createEmbedBuilder().apply(func).build())


fun CommandEvent.messageReply(func: EmbedBuilder.() -> Unit)
    = message.reply(
        createEmbedBuilder().apply(func).build()
    ).mentionRepliedUser(false).queue()

/**
 * Modifies the current [RestAction] into a [RestPromise], allowing you to use [RestPromise.then] and [RestPromise.catch].
 */
fun <V> RestAction<V>.asPromise(): RestPromise<V> = RestPromise(this)

/**
 * Shortcut for [RestPromise.then]
 */
infix fun <V> RestAction<V>.then(callback: (V) -> Unit): RestPromise<V> = asPromise().then(callback)

/**
 * Shortcut for [RestPromise.catch]
 */
infix fun <V> RestAction<V>.catch(failure: (Throwable) -> Unit): RestPromise<V> = asPromise().catch(failure)

fun Instant.prettyPrint(): String {
    val instStrArr = this.toString().split("T")
    val date = instStrArr.first().split("-")
    val year = date[0]
    val month = date[1]
    val day = date[2]
    val time = instStrArr[1].split(".").first()

    return "$day/$month/$year, $time"
}

fun CommandClientBuilder.withVolteCommands(): CommandClientBuilder {
    arrayListOf<KClass<*>>(
        //owner
        EvalCommand::class, SetNameCommand::class,

        //operator
        OperatorCommand::class, AntilinkCommand::class,
        MassPingsCommand::class, AutoroleCommand::class,
        AutoQuoteCommand::class, SetPrefixCommand::class,
        WelcomeCommand::class,

        //utility
        SayCommand::class, InfoCommand::class,
        PingCommand::class, SilentSayCommand::class,
        SnowflakeCommand::class, BigEmojiCommand::class,
        NowCommand::class, PermissionsCommand::class,
        NatoCommand::class,

    ).forEach {

        addCommand(it.java.constructors[0].newInstance() as Command)

    }
    return this
}

fun GuildMessageReceivedEvent.createEmbedBuilder(content: String?) = DiscordUtil.createDefaultEmbed(content, member!!)
fun GuildMessageReceivedEvent.createEmbed(content: String) = DiscordUtil.createDefaultEmbed(content, member!!).build()
fun CommandEvent.createEmbedBuilder(content: String? = null): EmbedBuilder = DiscordUtil.createDefaultEmbed(content, member)
fun CommandEvent.createEmbed(content: String): MessageEmbed = this.createEmbedBuilder(content).build()


fun Member.getHighestRoleWithColor(): Role? = this.roles.firstOrNull { it.color != null }

fun Guild.getData(): GuildData {
    return Volte.db().getSettingsFor(id)
}

fun Guild.getWelcomeSettings(): WelcomeSettings {
    return Volte.db().getWelcomeSettingsFor(id)
}

fun Guild.getTags(): TagsRepository {
    return Volte.db().getTagsFor(id)
}

fun Guild.getSelfRoles(): SelfRoleRepository {
    return Volte.db().getSelfRolesFor(id)
}

fun Guild.getBlacklist(): BlacklistRepository {
    return Volte.db().getBlacklistFor(id)
}

fun <T> ResultSet.updateValueOf(col: SQLColumn<T>, value: T) {
    col.updateValue(this, value)
}

fun <T> ResultSet.valueOf(col: SQLColumn<T>): T {
    return col.getValue(this)
}
