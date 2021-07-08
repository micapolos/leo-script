package leo.term.compiler.python

data class Python(val string: String)
val String.python: Python get() = Python(this)

