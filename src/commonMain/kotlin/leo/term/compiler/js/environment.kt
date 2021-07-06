package leo.term.compiler.js

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

val jsEnvironment: Environment<Js>
	get() =
		Environment(
			{ literal -> typed(literal.js.nativeTerm, literal.typeLine) },
			{ typedTerm ->
				when (typedTerm.t) {
					type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
						typed(
							fn("(x=>y=>x+y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine)
						)
					else -> null
				}
			}
		)

val Literal.js: Js get() = toString().js
