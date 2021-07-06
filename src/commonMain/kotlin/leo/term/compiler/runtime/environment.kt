package leo.term.compiler.runtime

import leo.lineTo
import leo.numberTypeLine
import leo.term.compiler.Environment
import leo.term.fn
import leo.term.head
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.tail
import leo.term.typed.typed
import leo.type
import leo.typeLine

val thingEnvironment: Environment<Thing>
	get() =
		Environment(
			{ literal -> typed(literal.thing.nativeTerm, literal.typeLine) },
			{ typedTerm ->
				when (typedTerm.t) {
					type(numberTypeLine, "add" lineTo type(numberTypeLine)) ->
						typed(
							fn(fn(DoubleAddDoubleThing.nativeTerm)).invoke(typedTerm.v.tail).invoke(typedTerm.v.head),
							type(numberTypeLine))
					else -> null
				}
			})
