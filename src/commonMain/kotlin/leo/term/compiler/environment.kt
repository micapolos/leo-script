package leo.term.compiler

import leo.Literal
import leo.lineTo
import leo.numberName
import leo.numberTypeLine
import leo.term.anyDouble
import leo.term.anyFn
import leo.term.anyValue
import leo.term.fn
import leo.term.invoke
import leo.term.native
import leo.term.term
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.getOrNull
import leo.term.typed.make
import leo.term.typed.typed
import leo.term.typed.typedLine
import leo.term.value
import leo.term.variable
import leo.type

data class Environment<V>(
	val literalFn: (Literal) -> TypedLine<V>,
	val resolveOrNullFn: (TypedTerm<V>) -> TypedTerm<V>?)

val runtimeEnvironment: Environment<Any?> get() =
	Environment(
		{ literal -> typedLine(literal) },
		{ typedTerm ->
			when (typedTerm.t) {
				// TODO: Add level of indirection.
				type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
					typed(
						fn(
							fn(
						term(anyFn {
							value(1.variable).native.anyDouble
								.plus(value(0.variable).native.anyDouble)
								.anyValue
						}))
						).invoke(typedTerm.make("given").getOrNull(numberName)!!.v)
							.invoke(typedTerm.make("given").getOrNull("add")!!.getOrNull(numberName)!!.v),
						type(numberTypeLine))
				else -> null
			}
		})
