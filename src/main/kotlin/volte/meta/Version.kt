package volte.meta

object Version {

    const val major: Int = 4
    const val minor: Int = 0
    const val patch: Int = 0
    val releaseType: ReleaseType = ReleaseType.DEVELOPMENT
    fun asKotlinVersion(): KotlinVersion = KotlinVersion(major, minor, patch)
    fun formatted(): String = "$major.$minor.$patch-$releaseType"


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