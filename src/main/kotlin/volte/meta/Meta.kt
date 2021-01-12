package volte.meta

import com.google.gson.Gson
import com.jagrosh.jdautilities.command.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageAction
import volte.Volte
import volte.commands.cmds.operator.*
import volte.commands.cmds.owner.*
import volte.commands.cmds.utilities.*
import volte.database.entities.*
import volte.lib.db.SQLColumn
import volte.lib.kjda.KEmbedBuilder
import volte.meta.entities.Optional
import volte.meta.entities.RestPromise
import java.sql.ResultSet
import java.time.Instant

typealias static = JvmStatic

internal object Main {
    @static
    fun main(args: Array<out String>) {
        Volte.start()
    }
}

inline fun <reified T> Gson.fromJson(raw: String): T = this.fromJson(raw, T::class.java)

inline fun CommandEvent.reply(content: String, func: EmbedBuilder.() -> Unit): RestPromise<Message> {
    return createEmbedBuilder(content).apply(func).build().forwardTo(this.channel).asPromise()
}

inline fun buildEmbed(receiver: EmbedBuilder.() -> Unit): MessageEmbed {
    return newEmbed(receiver).build()
}

inline fun newEmbed(receiver: EmbedBuilder.() -> Unit): EmbedBuilder {
    return EmbedBuilder().apply(receiver)
}

inline fun String.fromEnd(func: String.() -> String): String = this.reversed().func().reversed()


infix fun <T> T?.hasValue(func: (T) -> Unit): Optional<T> = Optional(this).hasValue(func)


infix fun <T> T?.hasNoValue(func: () -> Unit): Optional<T> = Optional(this).hasNoValue(func)


fun <T> T?.optional(): Optional<T> = Optional of this

inline fun CommandEvent.reply(func: EmbedBuilder.() -> Unit): RestPromise<Message> =
    createEmbedBuilder().apply(func).build().forwardTo(event.channel).asPromise()

infix fun CommandEvent.replyInline(func: EmbedBuilder.() -> Unit): RestPromise<Message> =
    createEmbedBuilder().apply(func).forwardTo(message.channel).reference(message).asPromise()

inline infix fun CommandEvent.replyEmbedInline(content: String): RestPromise<Message> =
    this replyInline { setDescription(content) }

/**
 * Modifies the current [RestAction] into a [RestPromise], allowing you to use [RestPromise.then]
 * and [RestPromise.catch] as opposed to [RestAction.queue] callbacks.
 */
fun <V> RestAction<V>.asPromise(): RestPromise<V> = RestPromise of this

/**
 * Shortcut for [RestPromise.then]
 */
infix fun <V> RestAction<V>.then(callback: (V) -> Unit): RestPromise<V> = asPromise() then callback

/**
 * Shortcut for [RestPromise.catch]
 */
infix fun <V> RestAction<V>.catch(failure: (Throwable) -> Unit): RestPromise<V> = asPromise() catch failure

inline fun String.isNumeric(): Boolean = isNotEmpty() and trim().all(Char::isDigit)


fun Instant.prettyPrint(): String {
    val instStrArr = this.toString().split("T")
    val date = instStrArr.first().split("-")
    val time = instStrArr[1].split(".").first()

    return "${date[2]}/${date[1]}/${date[0]}, $time"
}

inline infix fun MessageEmbed.forwardTo(channel: MessageChannel): MessageAction = channel.sendMessage(this)


inline infix fun EmbedBuilder.forwardTo(channel: MessageChannel): MessageAction = this.build().forwardTo(channel)


inline fun EmbedBuilder.addField(name: String, value: Any, inline: Boolean = false): EmbedBuilder = addField(name, value.toString(), inline)


inline fun CommandClientBuilder.withVolteCommands(): CommandClientBuilder {
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
        NatoCommand::class, AvatarCommand::class,
        GuildInfoCommand::class,

        ).map { it.java.constructors[0].newInstance() as Command }
        .forEach(::addCommand)
    return this
}

inline fun GuildMessageReceivedEvent.createEmbedBuilder(content: String? = null) = DiscordUtil.createDefaultEmbed(content, member!!)

inline infix fun GuildMessageReceivedEvent.createEmbed(content: String) = DiscordUtil.createDefaultEmbed(content, member!!).build()

inline fun CommandEvent.createEmbedBuilder(content: String? = null): EmbedBuilder = DiscordUtil.createDefaultEmbed(content, member)

inline infix fun CommandEvent.createEmbed(content: String): MessageEmbed = this.createEmbedBuilder(content).build()

inline fun Member.getHighestRoleWithColor(): Optional<Role> = Optional.of(this.roles.firstOrNull { it.color != null })

inline fun Member.isOperator(): Boolean = DiscordUtil.isOperator(this)

inline infix fun Member.hasRole(id: String): Boolean = this.roles.map(Role::getId).any(id::equals)

inline infix fun Member.hasRole(role: Role): Boolean = this.roles.any(role::equals)

inline fun Guild.getData(): GuildData = Volte.db().getSettingsFor(id)


inline fun Guild.getWelcomeSettings(): WelcomeSettings = Volte.db().getWelcomeSettingsFor(id)


inline fun Guild.getTags(): TagsRepository = Volte.db().getTagsFor(id)


inline fun Guild.getSelfRoles(): SelfRoleRepository = Volte.db().getSelfRolesFor(id)


inline fun Guild.getBlacklist(): BlacklistRepository = Volte.db().getBlacklistFor(id)


inline fun <T> ResultSet.updateValueOf(col: SQLColumn<T>, value: T) = col.updateValue(this, value)


inline infix fun <T> ResultSet.valueOf(col: SQLColumn<T>): T = col.getValue(this)

