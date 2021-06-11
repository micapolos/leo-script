package leo.named.compiler

import leo.TypeLine
import leo.base.indexed
import leo.base.notNullOrError
import leo.base.runIf
import leo.expression.compiler.resolveGetOrNull
import leo.indexed.compiler.Binding
import leo.indexed.compiler.compileIndexOf
import leo.indexed.compiler.compileOnlyExpression
import leo.indexed.expression
import leo.indexed.invoke
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.expressionTuple
import leo.indexed.typed.onlyTypedOrNull
import leo.indexed.typed.typed
import leo.indexed.variable
import leo.named.typed.TypedExpression
import leo.named.typed.TypedStructure
import leo.named.typed.typed
import leo.onlyOrNull

val <T> TypedStructure<T>.resolve: TypedStructure<T>
	get() =
	null
		?: resolveGetOrNull
		?: this

// TODO: Simplify this method, it's terrible right now.
val <T> TypedStructure<T>.resolveGetOrNull: TypedStructure<T>? get() =
	typeStructure.resolveGetOrNull?.let { resolvedTypeStructure ->
		TODO()
	}

fun <T> IndexedValue<Binding>.apply(tuple: TypedTuple<T>): Typed<T> =
	typed(
		expression<T>(variable(index)).runIf(!value.isConstant) {
			expression(invoke(this, tuple.expressionTuple))
		},
		value.typeLine)

val <T> TypedTuple<T>.compileTyped: Typed<T>
	get() =
	onlyTypedOrNull.notNullOrError("$this not expression")

fun <T> Typed<T>.compileCast(typeLine: TypeLine): Typed<T> =
	typed(
		expression(typeLine.compileIndexOf(this.typeLine).indexed(expression)),
		typeLine)

val <T> TypedStructure<T>.compileOnlyExpression: TypedExpression<T> get() =
	typeStructure.compileOnlyExpression.let { typeLine ->
		typed(structure.expressionStack.onlyOrNull!!, typeLine)
	}
