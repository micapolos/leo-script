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
import leo.textTypeLine
import leo.type

val jsEnvironment: Environment<Js>
	get() =
		Environment(
			{ literal -> literal.js.nativeTerm },
			{ typedTerm ->
				when (typedTerm.t) {
					type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
						typed(
							fn("(x=>y=>x+y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine))
					type(numberTypeLine, "subtract" lineTo type(numberTypeLine)) ->
						typed(
							fn("(x=>y=>x-y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine))
					type(numberTypeLine, "multiply" lineTo type("by" lineTo type(numberTypeLine))) ->
						typed(
							fn("(x=>y=>x*y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine))
					type(textTypeLine, "append" lineTo type(textTypeLine)) ->
						typed(
							fn("(x=>y=>x+y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
							type(textTypeLine))
					else -> null
				}
			}
		)

val Literal.js: Js get() = toString().js
