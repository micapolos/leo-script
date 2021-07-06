package leo.term.compiler.scheme

import leo.Literal
import leo.lineTo
import leo.numberTypeLine
import leo.term.compiler.Environment
import leo.term.fn
import leo.term.get
import leo.term.head
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.tail
import leo.term.typed.typed
import leo.type
import leo.typeLine
import scheme.Scheme
import scheme.scheme

val schemeEnvironment: Environment<Scheme>
	get() =
	Environment(
		{ literal -> typed(literal.scheme.nativeTerm, literal.typeLine) },
		{ typedTerm ->
			when (typedTerm.t) {
				type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
					typed(
						fn("(lambda (x) (lambda (y) (+ x y)))".scheme.nativeTerm.invoke(get<Scheme>(0).tail).invoke(get<Scheme>(0).head)).invoke(typedTerm.v),
						type(numberTypeLine)
					)
				else -> null
			}
		}
	)

val Literal.scheme: Scheme get() = toString().scheme
