package leo.term.compiler.native

import leo.base.assertEqualTo
import leo.term.fn
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.nativeValue
import kotlin.test.Test

class EvaluatorTest {
	@Test
	fun double() {
		10.0.native.nativeTerm
			.value
			.assertEqualTo(10.0.native.nativeValue)
	}

	@Test
	fun string() {
		"Hello, world!".native.nativeTerm
			.value
			.assertEqualTo("Hello, world!".native.nativeValue)
	}

	@Test
	fun doubleAddDouble() {
		fn(fn(DoubleAddDoubleNative.nativeTerm))
			.invoke(10.0.native.nativeTerm)
			.invoke(20.0.native.nativeTerm)
			.value
			.assertEqualTo(30.0.native.nativeValue)
	}

	@Test
	fun doubleSubtractDouble() {
		fn(fn(DoubleSubtractDoubleNative.nativeTerm))
			.invoke(30.0.native.nativeTerm)
			.invoke(20.0.native.nativeTerm)
			.value
			.assertEqualTo(10.0.native.nativeValue)
	}

	@Test
	fun doubleMultiplyByDouble() {
		fn(fn(DoubleMultiplyByDoubleNative.nativeTerm))
			.invoke(10.0.native.nativeTerm)
			.invoke(20.0.native.nativeTerm)
			.value
			.assertEqualTo(200.0.native.nativeValue)
	}

	@Test
	fun stringAppendString() {
		fn(fn(StringAppendStringNative.nativeTerm))
			.invoke("Hello, ".native.nativeTerm)
			.invoke("world!".native.nativeTerm)
			.value
			.assertEqualTo("Hello, world!".native.nativeValue)
	}
}