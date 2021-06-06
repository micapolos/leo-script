package scheme

import leo.Stack
import leo.Text
import leo.array
import leo.literalString
import leo.map
import leo.string

data class Scheme(val string: String)
val String.scheme get() = Scheme(this)

val Text.scheme: Scheme get() = string.literalString.scheme
val Int.scheme: Scheme get() = toString().scheme
val Double.scheme: Scheme get() = toString().scheme
val Boolean.scheme: Scheme get() = if (this) "#t".scheme else "#f".scheme

val Stack<Scheme>.schemeSpaced: Scheme get() =
	map { string }.array.joinToString(" ").scheme

val Scheme.parenthesize: Scheme get() =
	"($string)".scheme
