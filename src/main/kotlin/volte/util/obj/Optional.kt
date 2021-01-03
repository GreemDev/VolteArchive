package volte.util.obj

import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException

data class Optional<T>(private var value: T? = null) {

    companion object {
        fun <T> empty(): Optional<T> = Optional()
        infix fun <T> of(value: T?) = Optional(value)
    }


    infix fun hasValue(func: (T) -> Unit): Optional<T> {
        if (hasValue())
            func(value!!)

        return this
    }

    infix fun hasNoValue(func: () -> Unit): Optional<T> {
        if (!hasValue())
            func()

        return this
    }

    fun hasValue(): Boolean = value != null

    fun setValue(newValue: T? = null) {
        value = newValue
    }

    @Throws(IllegalStateException::class)
    fun value(): T {
        return if (value != null) value!! else
            throw IllegalStateException("The value this Optional holds is null; can't return the value.")
    }

    fun valueOrDefault(default: T): T {
        return value ?: default
    }

    fun valueOrNull(): T? {
        return value
    }

    override fun hashCode(): Int {
        return if (hasValue()) value.hashCode() else 0
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Optional<*>) return false
        if (!hasValue() || !other.hasValue()) return false

        return other.value() == this.value()
    }

    override fun toString(): String =
        if (hasValue()) value.toString()
        else throw InvalidStateException("Cannot toString() a null value!")

}