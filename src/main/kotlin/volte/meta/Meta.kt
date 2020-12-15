package volte.meta

import com.jagrosh.easysql.SQLColumn
import com.jagrosh.jdautilities.command.Command.Category
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.requests.RestAction
import volte.Volte
import volte.database.GuildData
import volte.database.VolteDatabase
import volte.entities.RestPromise
import java.awt.Color

object Constants {

    fun ownerCategory(): Category = Category("Owner") { event ->
        Volte.config().owner() == event.author.id
    }

    fun operatorCategory(): Category = Category("Operator") { event ->
        val data = Volte.db().getAllSettingsFor(event.guild.id)
        (event.member.isOwner or (event.member.hasPermission(Permission.ADMINISTRATOR)) or event.member.roles.any { role ->
            role.id == data.getOperator()
        })
    }

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
fun <V> RestAction<V>.catch(failure: (Throwable) -> Unit): RestPromise<V> = asPromise().catch(failure)



fun CommandEvent.createEmbed(content: String): MessageEmbed = this.createEmbedBuilder(content).build()

fun CommandEvent.createEmbedBuilder(content: String? = null): EmbedBuilder = EmbedBuilder()
    .setColor(member.getHighestRoleWithColor()?.color ?: Color.GREEN)
    .setFooter("Requested by ${author.asTag}", author.effectiveAvatarUrl)
    .setDescription(content ?: "")


fun Member.getHighestRoleWithColor(): Role? = this.roles.firstOrNull { it.color != null }

fun Guild.getData(): GuildData {
    return Volte.db().getAllSettingsFor(id)
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
