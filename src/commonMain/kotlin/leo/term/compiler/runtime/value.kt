package leo.term.compiler.runtime

import leo.term.Scope
import leo.term.Term
import leo.term.Value
import leo.term.native
import leo.term.nativeValue
import leo.term.value
import leo.term.variable

val Term<Thing>.value: Value<Thing> get() = thingEvaluator.value(this)

fun Scope<Thing>.value(thing: Thing): Value<Thing> =
	when (thing) {
		is DoubleThing -> thing.nativeValue
		is StringThing -> thing.nativeValue
		DoubleAddDoubleThing -> value(variable(1)).native.double.plus(value(variable(0)).native.double).thing.nativeValue
		DoubleSubtractDoubleThing -> value(variable(1)).native.double.minus(value(variable(0)).native.double).thing.nativeValue
		DoubleMultiplyByDoubleThing -> value(variable(1)).native.double.times(value(variable(0)).native.double).thing.nativeValue
		StringAppendStringThing -> value(variable(1)).native.string.plus(value(variable(0)).native.string).thing.nativeValue
	}
