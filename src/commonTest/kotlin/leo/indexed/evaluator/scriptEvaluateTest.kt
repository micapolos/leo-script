package leo.indexed.evaluator

import leo.base.assertEqualTo
import leo.indexed.evalutor.evaluate
import leo.lineTo
import leo.literal
import leo.script
import kotlin.test.Test

class ScriptEvaluateTest {
	@Test
	fun literals() {
		script(literal(10)).evaluate.assertEqualTo(10.0)
		script(literal("foo")).evaluate.assertEqualTo("foo")
	}

	@Test
	fun structure() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))))
			.evaluate
			.assertEqualTo(listOf(10.0, 20.0))
	}

	@Test
	fun get() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))),
			"x" lineTo script())
			.evaluate
			.assertEqualTo(10.0)
	}
}