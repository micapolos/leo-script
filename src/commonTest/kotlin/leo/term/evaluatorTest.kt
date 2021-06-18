package leo.term

import leo.base.assertEqualTo
import kotlin.test.Test

class EvaluatorTest {
	@Test
	fun literals() {
		nativeEvaluator
			.value("foo".native.term)
			.assertEqualTo("foo".native.value)
	}

	@Test
	fun stringPlusString() {
		nativeEvaluator
			.value(fn(fn(StringPlusNative.term)).invoke("Hello, ".native.term).invoke("world!".native.term))
			.assertEqualTo("Hello, world!".native.value)
	}
}