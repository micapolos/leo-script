package leo.term.evaluated

import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.term.compiled.Expression

data class Evaluated<out V>(val value: Value<V>, val type: Type)

sealed class Value<out T>
data class TupleValue<T>(val tuple: Tuple<T>): Value<T>()
data class SelectValue<T>(val select: Select<T>): Value<T>()

data class Tuple<T>(val lineStack: Stack<Line<T>>)

sealed class Line<out V>
data class NativeLine<V>(val native: V): Line<V>()
data class FieldLine<V>(val field: Field<V>): Line<V>()
data class FunctionLine<V>(val function: Function<V>): Line<V>()

data class Field<out V>(val name: String, val rhs: Evaluated<V>)
data class Select<out V>(val choice: TypeChoice, val index: Int, val line: Line<V>)
data class Function<out V>(val scope: Scope<V>, val expression: Expression<V>)

data class Scope<out V>(val evaluatedStack: Stack<Evaluated<V>>)
