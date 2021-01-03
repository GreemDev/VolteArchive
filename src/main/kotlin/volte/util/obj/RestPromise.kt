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


    private var success: Optional<(V) -> Unit> = Optional.empty()
    private var failure: Optional<(Throwable) -> Unit> = Optional.empty()


    init {
        action.queue({
            success ifPresent { success ->
                success(it)
            }

        }) {
            failure ifPresent { failure ->
                failure(it)
            }
        }
    }


    infix fun then(callback: (V) -> Unit): RestPromise<V> {
        success = Optional of callback
        return this
    }

    infix fun catch(callback: (Throwable) -> Unit): RestPromise<V> {
        failure = Optional of callback
        return this
    }

}