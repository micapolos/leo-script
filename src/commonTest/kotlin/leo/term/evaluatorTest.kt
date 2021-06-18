package leo.term

import leo.base.assertEqualTo
import kotlin.test.Test

class EvaluatorTest {
	@Test
	fun literals() {
		anyEvaluator
			.value("foo".anyTerm)
			.assertEqualTo("foo".anyValue)
	}

	@Test
	fun stringPlusString() {
		val stringPlusStringFn = anyFn {
			value(1.variable).native.anyString
				.plus(value(0.variable).native.anyString)
				.anyValue
		}

		anyEvaluator
			.value(
				fn(fn(stringPlusStringFn.anyTerm))
				.invoke("Hello, ".anyTerm)
				.invoke("world!".anyTerm))
			.assertEqualTo("Hello, world!".anyValue)
	}
}