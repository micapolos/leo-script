package leo.term.compiler.python

import leo.Literal
import leo.lineTo
import leo.numberTypeLine
import leo.term.compiler.Environment
import leo.term.fn
import leo.term.get
import leo.term.head
import leo.term.invoke
import leo.term.tail
import leo.term.term
import leo.term.typed.typed
import leo.type
import leo.typeLine


val pythonEnvironment: Environment<Python>
	get() =
		Environment(
			{ literal -> typed(literal.python.term, literal.typeLine) },
			{ typedTerm ->
				when (typedTerm.t) {
					type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
						typed(
							fn("(lambda x: lambda y: x + y)".python.term.invoke(get<Python>(0).tail).invoke(get<Python>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine)
						)
					else -> null
				}
			}
		)

val Literal.python: Python get() = toString().python
