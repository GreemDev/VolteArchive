package volte.entities

import net.dv8tion.jda.api.requests.RestAction
import volte.meta.asPromise

class RestPromise<V>(action: RestAction<V>) {

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