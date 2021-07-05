package leo.term.compiler.leo

import leo.Script
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
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

val scriptEnvironment: Environment<Script>
	get() =
		Environment(
			{ literal -> typed(script(literal).term, literal.typeLine) },
			{ typedTerm ->
				when (typedTerm.t) {
					type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
						typed(
							fn(script(
								"lambda" lineTo script(
									"lambda" lineTo script(
										"variable" lineTo script(literal(1)),
										"add" lineTo script("variable" lineTo script(literal(0))))))
								.term.invoke(get<Script>(0).tail).invoke(get<Script>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine)
						)
					else -> null
				}
			}
		)
