package leo.term.compiler.runtime

import leo.base.assertEqualTo
import leo.term.fn
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.nativeValue
import kotlin.test.Test

class EvaluatorTest {
	@Test
	fun double() {
		10.0.thing.nativeTerm
			.value
			.assertEqualTo(10.0.thing.nativeValue)
	}

	@Test
	fun string() {
		"Hello, world!".thing.nativeTerm
			.value
			.assertEqualTo("Hello, world!".thing.nativeValue)
	}

	@Test
	fun doubleAddDouble() {
		fn(fn(DoubleAddDoubleThing.nativeTerm))
			.invoke(10.0.thing.nativeTerm)
			.invoke(20.0.thing.nativeTerm)
			.value
			.assertEqualTo(30.0.thing.nativeValue)
	}

	@Test
	fun doubleSubtractDouble() {
		fn(fn(DoubleSubtractDoubleThing.nativeTerm))
			.invoke(30.0.thing.nativeTerm)
			.invoke(20.0.thing.nativeTerm)
			.value
			.assertEqualTo(10.0.thing.nativeValue)
	}

	@Test
	fun doubleMultiplyByDouble() {
		fn(fn(DoubleMultiplyByDoubleThing.nativeTerm))
			.invoke(10.0.thing.nativeTerm)
			.invoke(20.0.thing.nativeTerm)
			.value
			.assertEqualTo(200.0.thing.nativeValue)
	}

	@Test
	fun stringAppendString() {
		fn(fn(StringAppendStringThing.nativeTerm))
			.invoke("Hello, ".thing.nativeTerm)
			.invoke("world!".thing.nativeTerm)
			.value
			.assertEqualTo("Hello, world!".thing.nativeValue)
	}
}