package scheme

import leo.LeoObject
import leo.Stack
import leo.Text
import leo.array
import leo.lineTo
import leo.literal
import leo.literalString
import leo.map
import leo.script
import leo.string

data class Scheme(val string: String): LeoObject() {
  override val toScriptLine get() = "scheme" lineTo script(literal(string))
}

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

val nilScheme get() = scheme("`()")
fun spacedScheme(vararg schemes: Scheme): Scheme = schemes.joinToString(" ") { it.string }.scheme
fun scheme(vararg schemes: Scheme): Scheme = "(${spacedScheme(*schemes).string})".scheme
fun listScheme(vararg schemes: Scheme): Scheme = scheme(scheme("list"), *schemes)
fun vectorScheme(vararg schemes: Scheme): Scheme = scheme(scheme("vector"), *schemes)
fun pair(lhs: Scheme, rhs: Scheme): Scheme = scheme(scheme("cons"), lhs, rhs)
val Scheme.pairFirst: Scheme get() = scheme(scheme("car"), this)
val Scheme.pairSecond: Scheme get() = scheme(scheme("cdr"), this)

fun tupleScheme(vararg schemes: Scheme): Scheme =
  when (schemes.size) {
    0 -> nilScheme
    1 -> schemes[0]
    else -> vectorScheme(*schemes)
  }

fun Scheme.vectorRef(index: Scheme): Scheme =
  scheme(scheme("vector-ref"), this, index)

fun Scheme.case(vararg schemes: Scheme): Scheme =
  scheme(scheme("case"), this, *schemes)

fun Scheme.indexSwitch(vararg schemes: Scheme): Scheme =
  if (schemes.size == 2) scheme(scheme("if"), this, schemes[0], schemes[1])
  else case(*schemes.mapIndexed { index, scheme -> scheme(scheme(index), scheme) }.toTypedArray())
