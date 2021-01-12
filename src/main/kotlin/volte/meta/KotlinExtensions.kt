@file:Suppress("PackageDirectoryMismatch")

package kotlin.text

public inline fun buildString(initialValue: String, builderAction: StringBuilder.() -> Unit): String {
    return StringBuilder(initialValue).apply(builderAction).toString()
}