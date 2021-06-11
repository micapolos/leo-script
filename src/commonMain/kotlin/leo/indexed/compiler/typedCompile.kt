package leo.indexed.compiler

import leo.atomOrNull
import leo.base.notNullOrError
import leo.base.runIf
import leo.fieldOrNull
import leo.indexed.at
import leo.indexed.expression
import leo.indexed.getIndexedLineOrNull
import leo.indexed.invoke
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.expressionTuple
import leo.indexed.typed.of
import leo.indexed.typed.onlyTypedOrNull
import leo.indexed.typed.tuple
import leo.indexed.typed.typed
import leo.indexed.variable
import leo.onlyOrNull
import leo.recursible

val <T> TypedTuple<T>.resolve: TypedTuple<T> get() =
	null
		?: resolveGetOrNull
		?: this

val <T> TypedTuple<T>.resolveGetOrNull: TypedTuple<T>? get() =
	typedStack.onlyOrNull?.let { typed ->
		typed.typeLine.recursible.atomOrNull?.fieldOrNull?.let { typeField ->
			typeField.rhsType.getIndexedLineOrNull(typeField.name)?.let { indexedTypeLine ->
				tuple(expression(at(typed.expression, expression(indexedTypeLine.index))).of(indexedTypeLine.value))
			}
		}
	}

fun <T> IndexedValue<Binding>.apply(tuple: TypedTuple<T>): Typed<T> =
	typed(
		expression<T>(variable(index)).runIf(!value.isConstant) {
			expression(invoke(this, tuple.expressionTuple))
		},
		value.typeLine)

val <T> TypedTuple<T>.compileTyped: Typed<T> get() =
	onlyTypedOrNull.notNullOrError("$this not expression")