package leo.term.compiler.python

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
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

val pythonEnvironment: Environment<Python>
	get() =
		Environment(
			{ scriptLine ->
				when (scriptLine) {
					is FieldScriptLine -> null
					is LiteralScriptLine -> typed(scriptLine.literal.python.nativeTerm, scriptLine.literal.typeLine)
				}
			},
			{ typedTerm ->
				when (typedTerm.t) {
					type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
						typed(
							fn("(lambda x: lambda y: x + y)".python.nativeTerm.invoke(get<Python>(0).tail).invoke(get<Python>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine)
						)
					else -> null
				}
			}
		)

val Literal.python: Python get() = toString().python
