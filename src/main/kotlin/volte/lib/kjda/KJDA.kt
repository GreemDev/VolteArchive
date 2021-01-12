package volte.lib.kjda

import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.sharding.ShardManager

inline infix fun ShardManager.activity(lazy: () -> Activity): ShardManager {
    setPresence(OnlineStatus.ONLINE, lazy())
    return this
}

inline infix fun ShardManager.status(lazy: () -> OnlineStatus): ShardManager {
    setStatus(lazy())
    return this
}

inline infix fun ShardManager.presence(lazy: () -> Pair<OnlineStatus, Activity>): ShardManager {
    val presence = lazy()
    setPresence(presence.first, presence.second)
    return this
}

inline infix fun ShardManager.idle(lazy: () -> Boolean): ShardManager {
    setIdle(lazy())
    return this
}

inline operator fun <reified T : ShardManager> T.plusAssign(other: Any) { addEventListener(other) }