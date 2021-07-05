package leo.term.compiler.scheme

import leo.Literal
import leo.lineTo
import leo.numberTypeLine
import leo.term.compiler.Environment
import leo.term.head
import leo.term.invoke
import leo.term.tail
import leo.term.term
import leo.term.typed.typed
import leo.type
import leo.typeLine
import scheme.Scheme
import scheme.scheme

val schemeEnvironment: Environment<Scheme>
	get() =
	Environment(
		{ literal -> typed(literal.scheme.term, literal.typeLine) },
		{ typedTerm ->
			when (typedTerm.t) {
				type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
					typed(
						"(lambda (x) (lambda (y) (+ x y)))".scheme.term.invoke(typedTerm.v.tail).invoke(typedTerm.v.head),
						type(numberTypeLine)
					)
				else -> null
			}
		}
	)

val Literal.scheme: Scheme get() = toString().scheme
