package leo.term

import leo.base.stak.Stak
import leo.base.stak.push
import leo.base.stak.stakOf
import leo.base.stak.top

data class Scope<out T>(val valueStak: Stak<Value<T>>) { override fun toString() = scriptLine.toString() }

fun <T> scope(vararg values: Value<T>) =
	Scope(stakOf(*values))

fun <T> Scope<T>.plus(value: Value<T>): Scope<T> =
	valueStak.push(value).let(::Scope)

fun <T> Scope<T>.value(variable: TermVariable): Value<T> =
	valueStak.top(variable.index)!!