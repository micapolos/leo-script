package scheme

import leo.Number
import leo.Text
import leo.literalString

data class Scheme(val string: String)
val String.scheme get() = Scheme(this)

val Text.scheme: Scheme get() = string.literalString.scheme
val Number.scheme: Scheme get() = toString().scheme
val Boolean.scheme: Scheme get() = if (this) "#t".scheme else "#f".scheme
