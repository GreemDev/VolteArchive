package volte.util.obj

import net.dv8tion.jda.api.requests.RestAction
import volte.meta.asPromise

data class RestPromise<V>(private val action: RestAction<V>) {

    companion object {
        fun allOf(actions: MutableList<RestAction<*>>): RestPromise<*> {
            if (actions.size == 0) {
                throw IllegalArgumentException("List of RestActions must have elements.")
            }

            return if (actions.size == 1) {
                actions.first().asPromise()
            } else {
                RestAction.allOf(actions).asPromise()
            }
        }

        fun allOf(vararg actions: RestAction<*>): RestPromise<*> {
            return allOf(actions.toMutableList())
        }
    }


    private var success: (V) -> Unit = { }
    private var failure: (Throwable) -> Unit = { }


    init {
        action.queue({
            success(it)
        }, {
            failure(it)
        })
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