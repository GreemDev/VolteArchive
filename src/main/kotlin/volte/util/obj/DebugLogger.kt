package volte.util.obj

import net.dv8tion.jda.api.events.DisconnectEvent
import net.dv8tion.jda.api.events.RawGatewayEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.ReconnectedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import volte.Volte
import volte.meta.Version

class DebugLogger : ListenerAdapter() {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger("Gateway")
    }


    override fun onReconnected(event: ReconnectedEvent) {
        logger.info("Volte v${Version.formatted()} reconnected to Discord gateway.")
    }

    override fun onReady(event: ReadyEvent) {
        logger.info("Volte v${Version.formatted()} READY on Discord gateway.")
    }

    override fun onDisconnect(event: DisconnectEvent) {
        logger.info("Volte v${Version.formatted()} disconnected from Discord gateway.")
    }

    override fun onRawGateway(event: RawGatewayEvent) {
        val raw = event.payload
        logger.info("${event.type}: ${raw.toJson().joinToString()}")
    }

}