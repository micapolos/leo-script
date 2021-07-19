package leo.typed.indexed

import leo.IndexVariable
import leo.base.stak.Stak
import leo.base.stak.push
import leo.base.stak.stakOf
import leo.base.stak.top

data class ValueScope<out V>(val valueStak: Stak<Value<V>>)
fun <V> scope(vararg values: Value<V>) = ValueScope(stakOf(*values))
fun <V> ValueScope<V>.plus(value: Value<V>) = valueStak.push(value).let(::ValueScope)
fun <V> ValueScope<V>.value(variable: IndexVariable): Value<V> = valueStak.top(variable.index)!!