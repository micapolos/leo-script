package leo.term

import leo.base.stak.Stak
import leo.base.stak.push
import leo.base.stak.stakOf
import leo.base.stak.top
import leo.named.value.anyScriptLine

data class Scope<out T>(val valueStak: Stak<Value<T>>) {
  override fun toString() = scriptLine { it.anyScriptLine }.toString()
}

fun <T> scope(vararg values: Value<T>) =
  Scope(stakOf(*values))

fun <T> Scope<T>.plus(value: Value<T>): Scope<T> =
  valueStak.push(value).let(::Scope)

fun <T> Scope<T>.value(variable: IndexVariable): Value<T> =
  valueStak.top(variable.index)!!