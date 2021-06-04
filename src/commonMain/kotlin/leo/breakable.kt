package leo

data class Breakable<out T>(val value: T, val shouldBreak: Boolean)
fun <T> T.breakable(shouldBreak: Boolean) = Breakable(this, shouldBreak)

