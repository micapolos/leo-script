package leo.term.compiler.js

data class Js(val string: String)

val String.js: Js get() = Js(this)
