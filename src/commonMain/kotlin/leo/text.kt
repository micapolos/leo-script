package leo

@kotlin.jvm.JvmInline value class Text(val string: String)
val String.text: Text get() = Text(this)
