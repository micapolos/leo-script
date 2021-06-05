package leo

data class Text(val string: String)
val String.text: Text get() = Text(this)
