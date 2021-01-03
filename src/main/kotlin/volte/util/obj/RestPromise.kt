package volte.util.obj

import net.dv8tion.jda.api.requests.RestAction
import volte.meta.asPromise

data class RestPromise<V>(private val action: RestAction<V>) {

    companion object {
        infix fun allOf(actions: List<RestAction<*>>): RestPromise<*> {
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

        infix fun <V> of(action: RestAction<V>) = RestPromise(action)
    }


    private var success = Optional.empty<(V) -> Unit>()
    private var failure = Optional.empty<(Throwable) -> Unit>()


    init {
        action.queue({ entity ->
            success hasValue { success ->
                success(entity)
            }

        }) { throwable ->
            failure hasValue { failure ->
                failure(throwable)
            }
        }
    }


    infix fun then(callback: (V) -> Unit): RestPromise<V> {
        success.setValue(callback)
        return this
    }

    infix fun catch(callback: (Throwable) -> Unit): RestPromise<V> {
        failure.setValue(callback)
        return this
    }

}