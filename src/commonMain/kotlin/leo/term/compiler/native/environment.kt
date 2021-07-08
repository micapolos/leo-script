package leo.term.compiler.native

import leo.lineTo
import leo.numberTypeLine
import leo.term.compiler.Environment
import leo.term.compiler.equalsTypeLine
import leo.term.fn
import leo.term.get
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
							fn(fn(fn(DoubleAddDoubleNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine))
					type(numberTypeLine, "minus" lineTo type(numberTypeLine)) ->
						typed(
							fn(fn(fn(DoubleSubtractDoubleNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine))
					type(numberTypeLine, "times" lineTo type(numberTypeLine)) ->
						typed(
							fn(fn(fn(DoubleMultiplyByDoubleNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)).invoke(typedTerm.v),
							type(numberTypeLine))
					// TODO: Could it work for any object?
					type(numberTypeLine, "equals" lineTo type(numberTypeLine)) ->
						typed(
							fn(fn(fn(ObjectEqualsObjectNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)).invoke(typedTerm.v),
							type(equalsTypeLine))
					type(textTypeLine, "plus" lineTo type(textTypeLine)) ->
						typed(
							fn(fn(fn(StringAppendStringNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)).invoke(typedTerm.v),
							type(textTypeLine))
					type("length" lineTo type(textTypeLine)) ->
						typed(
							fn(StringLengthNative.nativeTerm).invoke(typedTerm.v),
							type("length" lineTo type(numberTypeLine)))
					else -> null
				}
			})
