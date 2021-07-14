package leo.term.indexed

import leo.Empty

sealed class Value<out V>

data class NativeValue<out V>(val native: V): Value<V>()
data class EmptyValue<out V>(val empty: Empty): Value<V>()
data class TupleValue<out V>(val tuple: ValueTuple<V>): Value<V>()
data class BooleanValue<out V>(val boolean: Boolean): Value<V>()
data class IndexValue<out V>(val index: Int): Value<V>()
data class FunctionValue<out V>(val function: ValueFunction<V>): Value<V>()
data class RecursiveValue<out V>(val recursive: ValueRecursive<V>): Value<V>()

data class ValueTuple<out V>(val valueList: List<Value<V>>)
data class ValueIndexed<out V>(val index: Int, val value: Value<V>)
data class ValueFunction<out V>(val scope: ValueScope<V>, val expression: Expression<V>)
data class ValueRecursive<out V>(val function: ValueFunction<V>)

fun <V> nativeValue(native: V): Value<V> = NativeValue(native)
fun <V> value(empty: Empty): Value<V> = EmptyValue(empty)
fun <V> value(tuple: ValueTuple<V>): Value<V> = TupleValue(tuple)
fun <V> value(boolean: Boolean): Value<V> = BooleanValue(boolean)
fun <V> value(index: Int): Value<V> = IndexValue(index)
fun <V> value(function: ValueFunction<V>): Value<V> = FunctionValue(function)
fun <V> value(recursive: ValueRecursive<V>): Value<V> = RecursiveValue(recursive)

fun <V> tuple(vararg values: Value<V>) = ValueTuple(listOf(*values))
fun <V> value(vararg values: Value<V>) = value(tuple(*values))
fun <V> function(scope: ValueScope<V>, expression: Expression<V>) = ValueFunction(scope, expression)
fun <V> recursive(function: ValueFunction<V>) = ValueRecursive(function)

val <V> Value<V>.native: V get() = (this as NativeValue).native
val <V> Value<V>.boolean: Boolean get() = (this as BooleanValue).boolean
val <V> Value<V>.index: Int get() = (this as IndexValue).index
fun <V> Value<V>.get(index: Int): Value<V> = (this as TupleValue).tuple.valueList[index]

val <V> ValueFunction<V>.recursive get() = copy(scope = scope.plus(value(recursive(this))))