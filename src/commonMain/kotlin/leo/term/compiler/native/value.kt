package leo.term.compiler.native

import leo.term.Scope
import leo.term.Term
import leo.term.Value
import leo.term.isValue
import leo.term.native
import leo.term.nativeValue
import leo.term.value
import leo.term.variable

val Term<Native>.value: Value<Native> get() = nativeEvaluator.value(this)

fun Scope<Native>.value(aNative: Native): Value<Native> =
	when (aNative) {
		is DoubleNative -> aNative.nativeValue
		is StringNative -> aNative.nativeValue
		DoubleAddDoubleNative -> value(variable(1)).native.double.plus(value(variable(0)).native.double).native.nativeValue
		DoubleSubtractDoubleNative -> value(variable(1)).native.double.minus(value(variable(0)).native.double).native.nativeValue
		DoubleMultiplyByDoubleNative -> value(variable(1)).native.double.times(value(variable(0)).native.double).native.nativeValue
		StringAppendStringNative -> value(variable(1)).native.string.plus(value(variable(0)).native.string).native.nativeValue
		ObjectEqualsObjectNative -> value(variable(1)).native.equals(value(variable(0)).native).isValue()
		StringLengthNative -> value(variable(0)).native.string.length.toDouble().native.nativeValue
	}
