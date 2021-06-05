package scheme

data class Scheme(val string: String)
val String.scheme get() = Scheme(this)