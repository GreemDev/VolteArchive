package volte.lib.meta.entities

import net.dv8tion.jda.api.events.DisconnectEvent
import net.dv8tion.jda.api.events.RawGatewayEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.ReconnectedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import volte.lib.meta.*

class DebugLogger : ListenerAdapter() {

    companion object {
        private fun logger(func: Logger.() -> Unit) {
            LoggerFactory.getLogger("Gateway").func()
        }
    }


    override fun onReconnected(event: ReconnectedEvent) {
        logger {
            info("Volte v${Version.formatted()} reconnected to Discord gateway.")
        }
    }

    override fun onReady(event: ReadyEvent) {
        logger {
            info("Volte v${Version.formatted()} READY on Discord gateway.")
        }
    }

    override fun onDisconnect(event: DisconnectEvent) {
        logger {
            info("Volte v${Version.formatted()} disconnected from Discord gateway.")
        }
    }

    override fun onRawGateway(event: RawGatewayEvent) {
        logger {
            info("${event.responseNumber} ${event.type}: ${gson().toJson(event.payload.toMap())}")
        }
    }

}