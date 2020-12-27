package volte.util.obj

import net.dv8tion.jda.api.requests.RestAction
import volte.meta.asPromise

data class RestPromise<V>(private val action: RestAction<V>) {

    companion object {
        fun allOf(actions: List<RestAction<*>>): RestPromise<*> {
            if (actions.isEmpty()) {
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


    private var success: ((V) -> Unit)? = null
    private var failure: ((Throwable) -> Unit)? = null


    init {
        action.queue({
            if (success != null) {
                success!!.invoke(it)
            }

        }, {
            if (failure != null) {
                failure!!.invoke(it)
            }
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