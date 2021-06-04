package leo

data class Endable<out T>(val value: T, val shouldEnd: Boolean)
fun <T> T.endable(shouldEnd: Boolean) = Endable(this, shouldEnd)

