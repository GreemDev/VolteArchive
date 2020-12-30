package volte.util.obj

import java.lang.IllegalStateException
import kotlin.jvm.Throws

class Optional<T>(private var value: T? = null) {

    companion object {
        fun <T> empty(): Optional<T> = Optional()
        fun <T> of(value: T?) = Optional(value)
    }


    infix fun ifPresent(func: (T) -> Unit) {
        if (value != null)
            func(value!!)
    }

    infix fun ifNotPresent(func: () -> Unit) {
        if (value == null)
            func()
    }

    fun setValue(newValue: T? = null) {
        value = newValue
    }

    @Throws(IllegalStateException::class)
    fun value(): T {
        return if (value == null) {
            throw IllegalStateException("The value this Optional holds is null; can't return the value.")
        } else {
            value!!
        }
    }

    fun valueOrDefault(default: T): T {
        return try {
            value()
        } catch (e: IllegalStateException) {
            default
        }
    }

    fun valueOrNull(): T? {
        return try {
            value()
        } catch (e: IllegalStateException) {
            null
        }
    }

}