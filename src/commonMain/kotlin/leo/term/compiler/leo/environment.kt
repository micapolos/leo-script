package leo.term.compiler.leo

import leo.FieldScriptLine
import leo.LiteralScriptLine
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
import leo.term.nativeTerm
import leo.term.tail
import leo.term.typed.typed
import leo.type
import leo.typeLine

val scriptEnvironment: Environment<Script>
	get() =
		Environment(
			{ scriptLine ->
				when (scriptLine) {
					is FieldScriptLine -> null
					is LiteralScriptLine -> typed(script(scriptLine.literal).nativeTerm, scriptLine.literal.typeLine)
				}
			},
			{ typedTerm ->
				when (typedTerm.t) {
					type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
						typed(
							fn(script(
								"lambda" lineTo script(
									"lambda" lineTo script(
										"variable" lineTo script(literal(1)),
										"add" lineTo script("variable" lineTo script(literal(0))))))
								.nativeTerm.invoke(get<Script>(0).tail).invoke(get<Script>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine)
						)
					else -> null
				}
			}
		)
