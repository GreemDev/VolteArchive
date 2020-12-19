package volte

typealias static = JvmStatic

internal object Main {

    @static fun main(args: Array<out String>) {
        Volte.start()
    }

}