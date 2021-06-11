package leo.indexed.evaluator

import leo.base.assertEqualTo
import leo.indexed.at
import leo.indexed.expression
import leo.indexed.tuple
import leo.literal
import kotlin.test.Test

class EvaluateTest {
	@Test
	fun vectorAt() {
		expression(
			at(
				expression<Any>(
					tuple(
						expression(literal("Hello, ")),
						expression(literal("world!")))),
				expression(1)))
			.evaluate
			.assertEqualTo("world!")
	}
}