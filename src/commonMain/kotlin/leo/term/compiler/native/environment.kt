package leo.term.compiler.native

import leo.lineTo
import leo.numberTypeLine
import leo.term.compiler.Environment
import leo.term.fn
import leo.term.head
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.tail
import leo.term.typed.typed
import leo.textTypeLine
import leo.type

val nativeEnvironment: Environment<Native>
	get() =
		Environment(
			{ literal -> literal.native.nativeTerm },
			{ typedTerm ->
				when (typedTerm.t) {
					type(numberTypeLine, "plus" lineTo type(numberTypeLine)) ->
						typed(
							fn(fn(DoubleAddDoubleNative.nativeTerm)).invoke(typedTerm.v.tail).invoke(typedTerm.v.head),
							type(numberTypeLine))
					type(numberTypeLine, "minus" lineTo type(numberTypeLine)) ->
						typed(
							fn(fn(DoubleSubtractDoubleNative.nativeTerm)).invoke(typedTerm.v.tail).invoke(typedTerm.v.head),
							type(numberTypeLine))
					type(numberTypeLine, "times" lineTo type(numberTypeLine)) ->
						typed(
							fn(fn(DoubleMultiplyByDoubleNative.nativeTerm)).invoke(typedTerm.v.tail).invoke(typedTerm.v.head),
							type(numberTypeLine))
					type(textTypeLine, "plus" lineTo type(textTypeLine)) ->
						typed(
							fn(fn(StringAppendStringNative.nativeTerm)).invoke(typedTerm.v.tail).invoke(typedTerm.v.head),
							type(textTypeLine))
					else -> null
				}
			})
