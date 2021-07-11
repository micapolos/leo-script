package scheme

import leo.Stack
import leo.Text
import leo.array
import leo.literalString
import leo.map
import leo.string

data class Scheme(val string: String)

fun scheme(string: String): Scheme = Scheme(string)
val String.scheme get() = Scheme(this)

val Text.scheme: Scheme get() = string.literalString.scheme
val Int.scheme: Scheme get() = toString().scheme
val Double.scheme: Scheme get() = toString().scheme
val Boolean.scheme: Scheme get() = if (this) "#t".scheme else "#f".scheme

val Stack<Scheme>.schemeSpaced: Scheme
  get() =
    map { string }.array.joinToString(" ").scheme

val Scheme.parenthesize: Scheme get() = "($string)".scheme
fun scheme(int: Int): Scheme = scheme("$int")

val nilScheme get() = listScheme()
fun spacedScheme(vararg schemes: Scheme): Scheme = schemes.joinToString(" ") { it.string }.scheme
fun scheme(vararg schemes: Scheme): Scheme = "(${spacedScheme(*schemes).string})".scheme
fun listScheme(vararg schemes: Scheme): Scheme = "`${scheme(*schemes).string}".scheme
fun vectorScheme(vararg schemes: Scheme): Scheme = "#${scheme(*schemes).string}".scheme
fun Scheme.plus(rhs: Scheme): Scheme = listScheme(this, scheme("."), rhs)
val Scheme.lhs: Scheme get() = scheme(scheme("cdr"), this)
val Scheme.rhs: Scheme get() = scheme(scheme("car"), this)

fun tupleScheme(vararg schemes: Scheme): Scheme =
  when (schemes.size) {
    0 -> nilScheme
    1 -> schemes[0]
    2 -> schemes[0].plus(schemes[1])
    else -> vectorScheme(*schemes)
  }

fun Scheme.vectorRef(index: Scheme): Scheme =
  scheme(scheme("vector-ref"), this, index)

fun Scheme.case(vararg schemes: Scheme): Scheme =
  scheme(scheme("case"), this, *schemes)

fun Scheme.indexSwitch(vararg schemes: Scheme): Scheme =
  case(*schemes.mapIndexed { index, scheme -> scheme(scheme(index), scheme) }.toTypedArray())
