package leo.term.compiler

import leo.Literal
import leo.Script
import leo.lineTo
import leo.numberTypeLine
import leo.term.anyDouble
import leo.term.anyFn
import leo.term.anyValue
import leo.term.fn
import leo.term.head
import leo.term.invoke
import leo.term.native
import leo.term.tail
import leo.term.term
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.typed
import leo.term.typed.typedLine
import leo.term.value
import leo.term.variable
import leo.type

data class Environment<V>(
	val literalFn: (Literal) -> TypedLine<V>,
	val resolveOrNullFn: (TypedTerm<V>) -> TypedTerm<V>?)

fun <V> Environment<V>.typedTerm(script: Script): TypedTerm<V> =
	Context(this, scope()).compileTypedTerm(script)

val runtimeEnvironment: Environment<Any?> get() =
	Environment(
		{ literal -> typedLine(literal) },
		{ typedTerm ->
			when (typedTerm.t) {
				type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
					typed(
						fn(
							fn(
								term(
									anyFn {
										value(1.variable).native.anyDouble
											.plus(value(0.variable).native.anyDouble)
											.anyValue
									}
								)
							)
							// TODO: Add level of indirection for binary operators.
						).invoke(typedTerm.v.tail).invoke(typedTerm.v.head),
						type(numberTypeLine))
				else -> null
			}
		})
