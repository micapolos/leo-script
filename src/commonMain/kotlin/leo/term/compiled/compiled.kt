package leo.term.compiled

import leo.Stack
import leo.Type
import leo.TypeLine

data class Compiled<out V>(val expression: Expression<V>, val type: Type)

data class Expression<out V>(val operationStack: Stack<Operation<V>>)

sealed class Operation<out V>
data class LineOperation<V>(val line: Line<V>): Operation<V>()
data class ContentOperation<V>(val content: Content): Operation<V>()
data class GetOperation<V>(val get: Get): Operation<V>()
data class DoOperation<V>(val do_: Do<V>): Operation<V>()
data class FunctionOperation<V>(val function: Function<V>): Operation<V>()
data class ApplyOperation<V>(val apply: Apply<V>): Operation<V>()
data class ResolveOperation<V>(val resolve: Resolve): Operation<V>()
data class LetOperation<V>(val let: Let<V>): Operation<V>()
data class PickOperation<V>(val pick: Pick<V>): Operation<V>()
data class DropOperation<V>(val drop: Drop<V>): Operation<V>()
data class SwitchOperation<V>(val switch: Switch<V>): Operation<V>()

sealed class Line<out V>
data class NativeLine<V>(val native: V): Line<V>()
data class FieldLine<V>(val field: Field<V>): Line<V>()

data class Case<out V>(val name: String, val body: Compiled<V>)

object Content
data class Apply<out V>(val rhs: Compiled<V>)
data class Do<out V>(val rhs: Compiled<V>)
data class Field<out V>(val name: String, val rhs: Compiled<V>)
data class Get(val name: String)
data class Resolve(val type: Type)
data class Let<out V>(val type: Type, val body: Compiled<V>)
data class Function<out V>(val type: Type, val body: Compiled<V>)
data class Pick<out V>(val line: Line<V>)
data class Drop<out V>(val typeLine: TypeLine)
data class Switch<out V>(val caseStack: Stack<Case<V>>)
