package volte.meta

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageAction
import volte.Volte
import volte.commands.cmds.operator.*
import volte.commands.cmds.owner.EvalCommand
import volte.commands.cmds.owner.SetNameCommand
import volte.commands.cmds.utilities.*
import volte.database.entities.*
import volte.lib.db.SQLColumn
import volte.util.DiscordUtil
import volte.util.obj.Optional
import volte.util.obj.RestPromise
import java.sql.ResultSet
import java.time.Instant

typealias static = JvmStatic

internal object Main {
    @static
    fun main(args: Array<out String>) {
        Volte.start()
    }
}

inline fun CommandEvent.reply(content: String, func: EmbedBuilder.() -> Unit) {
    createEmbedBuilder(content).apply(func).build().forwardTo(this.channel).queue()
}

fun String.fromEnd(func: String.() -> Unit): String {
    val value = this.reversed()
    value.apply(func)
    return value.reversed()
}

infix fun <T> T?.hasValue(func: (T) -> Unit): Optional<T> {
    return Optional(this).hasValue { func(it) }
}

infix fun <T> T?.hasNoValue(func: () -> Unit): Optional<T> {
    return Optional(this).hasNoValue { func() }
}

fun <T> T?.optional(): Optional<T> = Optional of this

inline fun CommandEvent.reply(func: EmbedBuilder.() -> Unit) = reply(createEmbedBuilder().apply(func).build())


infix fun CommandEvent.replyInline(func: EmbedBuilder.() -> Unit) =
    createEmbedBuilder().apply(func).forwardTo(message.channel).reference(message).queue()

/**
 * Modifies the current [RestAction] into a [RestPromise], allowing you to use [RestPromise.then] and [RestPromise.catch].
 */
fun <V> RestAction<V>.asPromise(): RestPromise<V> = RestPromise of this

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
    val time = instStrArr[1].split(".").first()

    return "${date[2]}/${date[1]}/${date[0]}, $time"
}

infix fun MessageEmbed.forwardTo(channel: MessageChannel): MessageAction {
    return channel.sendMessage(this)
}

infix fun EmbedBuilder.forwardTo(channel: MessageChannel): MessageAction {
    return this.build().forwardTo(channel)
}

fun CommandClientBuilder.withVolteCommands(): CommandClientBuilder {
    arrayListOf(
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
        NatoCommand::class, AvatarCommand::class

    ).map { it.java.constructors[0].newInstance() as Command }
        .forEach(this::addCommand)
    return this
}

fun GuildMessageReceivedEvent.createEmbedBuilder(content: String? = null) =
    DiscordUtil.createDefaultEmbed(content, member!!)

infix fun GuildMessageReceivedEvent.createEmbed(content: String) =
    DiscordUtil.createDefaultEmbed(content, member!!).build()

fun CommandEvent.createEmbedBuilder(content: String? = null): EmbedBuilder =
    DiscordUtil.createDefaultEmbed(content, member)

infix fun CommandEvent.createEmbed(content: String): MessageEmbed = this.createEmbedBuilder(content).build()


fun Member.getHighestRoleWithColor(): Optional<Role> = Optional.of(this.roles.firstOrNull { it.color != null })

fun Member.isOperator(): Boolean = DiscordUtil.isOperator(this)

fun Member.hasRole(id: String): Boolean {
    return this.roles.map(Role::getId).any(id::equals)
}

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
