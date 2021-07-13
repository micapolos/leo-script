package leo.term.indexed

sealed class Value<out V>

data class NativeValue<out V>(val native: V): Value<V>()
data class TupleValue<out V>(val tuple: ValueTuple<V>): Value<V>()
data class IndexValue<out V>(val index: Int): Value<V>()
data class IndexedValue<out V>(val indexed: ValueIndexed<V>): Value<V>()
data class FunctionValue<out V>(val function: ValueFunction<V>): Value<V>()
data class RecursiveValue<out V>(val recursive: ValueRecursive<V>): Value<V>()

data class ValueTuple<out V>(val valueList: List<Value<V>>)
data class ValueIndexed<out V>(val index: Int, val value: Value<V>)
data class ValueFunction<out V>(val scope: ValueScope<V>, val expression: Expression<V>)
data class ValueRecursive<out V>(val function: ValueFunction<V>)

fun <V> nativeValue(native: V): Value<V> = NativeValue(native)
fun <V> value(tuple: ValueTuple<V>): Value<V> = TupleValue(tuple)
fun <V> value(index: Int): Value<V> = IndexValue(index)
fun <V> value(indexed: ValueIndexed<V>): Value<V> = IndexedValue(indexed)
fun <V> value(function: ValueFunction<V>): Value<V> = FunctionValue(function)
fun <V> value(recursive: ValueRecursive<V>): Value<V> = RecursiveValue(recursive)

fun <V> tuple(vararg values: Value<V>) = ValueTuple(listOf(*values))
fun <V> value(vararg values: Value<V>) = value(tuple(*values))
fun <V> indexed(index: Int, value: Value<V>) = ValueIndexed(index, value)
fun <V> function(scope: ValueScope<V>, expression: Expression<V>) = ValueFunction(scope, expression)
fun <V> recursive(function: ValueFunction<V>) = ValueRecursive(function)

val <V> Value<V>.index: Int get() = (this as IndexValue).index
val <V> Value<V>.indexed: ValueIndexed<V> get() = (this as IndexedValue).indexed
fun <V> Value<V>.get(index: Int): Value<V> = (this as TupleValue).tuple.valueList[index]
