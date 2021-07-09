package leo.term.compiler.js

import leo.Literal

data class Js(val string: String)

val String.js: Js get() = Js(this)

val Literal.js: Js get() = toString().js
val trueJs: Js get() = "(f0=>f1=>f0(x=>x))".js
val falseJs: Js get() = "(f0=>f1=>f1(x=>x))".js
val Js.boolean: Js get() = "(${string}?${trueJs.string}:${falseJs.string})".js