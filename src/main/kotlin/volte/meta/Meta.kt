package volte.meta

import com.jagrosh.easysql.SQLColumn
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
import volte.commands.cmds.owner.EvalCommand
import volte.commands.cmds.utilities.InfoCommand
import volte.commands.cmds.utilities.PingCommand
import volte.database.entities.GuildData
import volte.entities.RestPromise
import volte.util.DiscordUtil

object Constants {
    fun ownerCategory(): Category = Category("Owner", DiscordUtil::isBotOwner)
    fun operatorCategory(): Category = Category("Operator", DiscordUtil::isOperator)
}

fun stopwatch(func: () -> Unit): Long {
    val start = System.currentTimeMillis()
    func()
    return System.currentTimeMillis() - start
}

fun CommandEvent.reply(content: String, func: EmbedBuilder.() -> Unit) {
    val e = createEmbedBuilder(content)
    func(e)
    reply(e.build())
}

fun CommandEvent.reply(func: EmbedBuilder.() -> Unit) {
    val e = createEmbedBuilder()
    func(e)
    reply(e.build())
}

fun CommandEvent.messageReply(func: EmbedBuilder.() -> Unit) {
    val e = createEmbedBuilder()
    func(e)
    message.reply(e.build()).queue()
}

fun <V> RestAction<V>.asPromise(): RestPromise<V> = RestPromise(this)

infix fun <V> RestAction<V>.then(callback: (V) -> Unit): RestPromise<V> = asPromise().then(callback)
infix fun <V> RestAction<V>.catch(failure: (Throwable) -> Unit): RestPromise<V> = asPromise().catch(failure)

fun CommandClientBuilder.withVolteCommands(): CommandClientBuilder {
    for (command in arrayListOf(
        OperatorCommand::class,
        InfoCommand::class,
        PingCommand::class,
        EvalCommand::class,
        SetPrefixCommand::class,
        AntilinkCommand::class,
        MassPingsCommand::class,
        AutoroleCommand::class,
        AutoQuoteCommand::class
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

fun SQLColumn<*>.equalsValue(str: String): String {
    return this.`is`(str)
}

fun SQLColumn<*>.equalsValue(int: Int): String {
    return this.`is`(int)
}

fun SQLColumn<*>.equalsValue(long: Long): String {
    return this.`is`(long)
}
