package leo.indexed.compiler

import leo.atomOrNull
import leo.fieldOrNull
import leo.indexed.at
import leo.indexed.expression
import leo.indexed.getIndexedLineOrNull
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.of
import leo.indexed.typed.tuple
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