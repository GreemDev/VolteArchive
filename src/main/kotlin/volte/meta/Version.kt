@file:Suppress("unused")

package volte.meta

object Version {

    const val major = 4
    const val minor = 0
    const val patch = 0
    val releaseType = ReleaseType.DEVELOPMENT
    fun asKotlinVersion() = KotlinVersion(major, minor, patch)
    fun formatted() = "$major.$minor.$patch-$releaseType"


}

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