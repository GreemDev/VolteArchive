package volte.lib.meta.entities

import net.dv8tion.jda.api.requests.RestAction

data class RestPromise<V> internal constructor(private val action: RestAction<V>, private val async: Boolean = true) {

    companion object {
        infix fun allOf(actions: List<RestAction<*>>): RestPromise<*> {
            if (actions.isEmpty()) {
                throw IllegalArgumentException("List of RestActions must have elements.")
            }

            return if (actions.size == 1) {
                RestPromise of actions.first()
            } else {
                RestPromise of RestAction.allOf(actions)
            }
        }

        fun allOf(vararg actions: RestAction<*>): RestPromise<*> {
            return allOf(*actions)
        }

        infix fun <V> of(action: RestAction<V>) = RestPromise(action)
    }


    private var success: (V) -> Unit = {}
    private var failure: (Throwable) -> Unit = {}


    init {
        if (async) {
            action.queue({ entity ->
                success(entity)

            }) { throwable ->
                failure(throwable)
            }
        } else {
            val result: V? = try {
                action.complete()
            } catch (t: Throwable) {
                failure(t)
                null
            }
            if (result != null) {
                success(result)
            }
        }

    }


    infix fun then(callback: (V) -> Unit): RestPromise<V> {
        success = callback
        return this
    }

    infix fun catch(callback: (Throwable) -> Unit): RestPromise<V> {
        failure = callback
        return this
    }

}