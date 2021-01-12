package volte.lib.kjda

import com.neovisionaries.ws.client.WebSocketFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.sharding.ShardManager
import okhttp3.OkHttpClient

/**
 * Constructs a new [DefaultShardManagerBuilder] and applies the specified
 * init function `DefaultShardManagerBuiilder.() -> Unit` to that receiver.
 * This uses [JDA.awaitReady] on every shard upon usage of [DefaultShardManagerBuilder.build].
 *
 * The token is not required here, however needs to be configured in the given function!
 *
 * @param[init]
 *       The function which uses the constructed DefaultShardManagerBuilder as the receiver to setup
 *       the JDA information before building it
 *
 * @sample client
 *
 * @see    DefaultShardManagerBuilder
 */
fun client(token: String, init: DefaultShardManagerBuilder.() -> Unit): ShardManager {
    return DefaultShardManagerBuilder.createDefault(token)
        .apply(init).build()
        .also { it.shards.forEach(JDA::awaitReady) }
}

/** Lazy infix overload for [DefaultShardManagerBuilder.setToken] */
inline infix fun DefaultShardManagerBuilder.token(lazyToken: () -> String): DefaultShardManagerBuilder
        = this.setToken(lazyToken())
/** Lazy infix overload for [DefaultShardManagerBuilder.setActivity] */
inline infix fun DefaultShardManagerBuilder.activity(lazy: () -> Activity): DefaultShardManagerBuilder
        = this.setActivity(lazy())
/** Lazy infix overload for [DefaultShardManagerBuilder.setStatus] */
inline infix fun DefaultShardManagerBuilder.status(lazy: () -> OnlineStatus): DefaultShardManagerBuilder
        = this.setStatus(lazy())
/** Lazy infix overload for [DefaultShardManagerBuilder.setEventManager] */
infix fun DefaultShardManagerBuilder.manager(lazy: (Int) -> IEventManager): DefaultShardManagerBuilder
        = this.setEventManagerProvider(lazy)
/** Lazy infix overload for [DefaultShardManagerBuilder.addEventListeners] */
inline infix fun DefaultShardManagerBuilder.listener(lazy: () -> EventListener): DefaultShardManagerBuilder
        = this.addEventListeners(lazy())
/** Lazy infix overload for [DefaultShardManagerBuilder.setAudioSendFactory] */
inline infix fun DefaultShardManagerBuilder.audioSendFactory(lazy: () -> IAudioSendFactory): DefaultShardManagerBuilder
        = this.setAudioSendFactory(lazy())

/** Infix overload for [DefaultShardManagerBuilder.setIdle] */
infix fun DefaultShardManagerBuilder.idle(lazy: Boolean): DefaultShardManagerBuilder
        = this.setIdle(lazy)
/** Infix overload for [DefaultShardManagerBuilder.setEnableShutdownHook] */
infix fun DefaultShardManagerBuilder.shutdownHook(lazy: Boolean): DefaultShardManagerBuilder
        = this.setEnableShutdownHook(lazy)

/** Infix overload for [DefaultShardManagerBuilder.setAutoReconnect] */
infix fun DefaultShardManagerBuilder.autoReconnect(lazy: Boolean): DefaultShardManagerBuilder
        = this.setAutoReconnect(lazy)

/**
 * Provides new WebSocketFactory and calls the provided lazy
 * initializer to allow setting options like timeouts
 */
inline infix fun DefaultShardManagerBuilder.websocketSettings(init: WebSocketFactory.() -> Unit): DefaultShardManagerBuilder {
    val factory = WebSocketFactory()
    factory.init()
    setWebsocketFactory(factory)
    return this
}

/**
 * Provides new OkHttpClient.Builder and calls the provided lazy
 * initializer to allow setting options like timeouts
 */
inline infix fun DefaultShardManagerBuilder.httpSettings(init: OkHttpClient.Builder.() -> Unit): DefaultShardManagerBuilder {
    val builder = OkHttpClient.Builder()
    builder.init()
    setHttpClientBuilder(builder)
    return this
}

/** Overload for [DefaultShardManagerBuilder.addEventListeners] */
fun DefaultShardManagerBuilder.listener(vararg listener: Any): DefaultShardManagerBuilder
        = this.addEventListeners(*listener)
/** Overload for [DefaultShardManagerBuilder.removeEventListeners] */
fun DefaultShardManagerBuilder.removeListener(vararg listener: Any): DefaultShardManagerBuilder
        = this.removeEventListeners(*listener)

/** Operator overload for [DefaultShardManagerBuilder.addEventListeners] */
operator fun DefaultShardManagerBuilder.plusAssign(other: Any) { listener(other) }

/** Operator overload for [DefaultShardManagerBuilder.removeEventListeners] */
operator fun DefaultShardManagerBuilder.minusAssign(other: Any) { removeListener(other) }