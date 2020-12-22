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
import net.dv8tion.jda.api.requests.RestAction
import volte.Volte
import volte.commands.cmds.operator.*
import volte.commands.cmds.owner.*
import volte.commands.cmds.utilities.*
import volte.database.entities.GuildData
import volte.util.obj.RestPromise
import volte.util.DiscordUtil
import java.sql.ResultSet
import java.time.Instant

object Constants {
    fun ownerCategory(): Category = Category("Owner", DiscordUtil::isBotOwner)
    fun operatorCategory(): Category = Category("Operator", DiscordUtil::isOperator)
    fun utilityCategory(): Category = Category("Utility")
}

fun CommandEvent.reply(content: String, func: EmbedBuilder.() -> Unit) {
    val e = createEmbedBuilder(content)
    func(e)
    reply(e.build())
}

fun CommandEvent.reply(func: EmbedBuilder.() -> Unit) = reply(createEmbedBuilder().apply(func).build())


fun CommandEvent.messageReply(func: EmbedBuilder.() -> Unit)
    = message.reply(
        createEmbedBuilder().apply(func).build()
    ).mentionRepliedUser(false).queue()

fun <V> RestAction<V>.asPromise(): RestPromise<V> = RestPromise(this)

infix fun <V> RestAction<V>.then(callback: (V) -> Unit): RestPromise<V> = asPromise().then(callback)
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
    for (command in arrayListOf(
        OperatorCommand::class, InfoCommand::class,
        PingCommand::class, EvalCommand::class,
        SetPrefixCommand::class, AntilinkCommand::class,
        MassPingsCommand::class, AutoroleCommand::class,
        AutoQuoteCommand::class, SayCommand::class,
        SilentSayCommand::class, WelcomeCommand::class,
        SnowflakeCommand::class, BigEmojiCommand::class,
        NowCommand::class, PermissionsCommand::class
    )) {
        addCommand(command.java.constructors[0].newInstance() as Command)
    }
    return this
}


fun CommandEvent.createEmbed(content: String): MessageEmbed = this.createEmbedBuilder(content).build()

fun CommandEvent.createEmbedBuilder(content: String? = null): EmbedBuilder =
    DiscordUtil.createDefaultEmbed(content, member)


fun Member.getHighestRoleWithColor(): Role? = this.roles.firstOrNull { it.color != null }

fun Guild.getData(): GuildData {
    return Volte.db().getSettingsFor(id)
}

fun <T> ResultSet.updateValueOf(col: SQLColumn<T>, value: T) {
    col.updateValue(this, value)
}

fun <T> ResultSet.valueOf(col: SQLColumn<T>): T {
    return col.getValue(this)
}
