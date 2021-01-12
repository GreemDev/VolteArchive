@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package volte.meta

fun KotlinVersion.formatted() = "$major.$minor.$patch"

object Version {

    private const val major = 4
    private const val minor = 0
    private const val patch = 0
    val releaseType = ReleaseType.DEVELOPMENT
    fun major() = major
    fun minor() = minor
    fun patch() = patch
    fun asKotlinVersion() = KotlinVersion(major, minor, patch)
    fun formatted() = "${asKotlinVersion().formatted()}-$releaseType"

    enum class ReleaseType {
        RELEASE,
        DEVELOPMENT;

        override fun toString(): String {
            return when (this) {
                RELEASE -> "Release"
                DEVELOPMENT -> "Development"
            }
        }
    }
}