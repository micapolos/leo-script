package leo.indexed.evaluator

import leo.base.assertEqualTo
import leo.line
import leo.lineTo
import leo.literal
import leo.script
import kotlin.test.Test

class ScriptEvaluateLineTest {
	@Test
	fun literals() {
		script(literal(10))
			.evaluateLine
			.assertEqualTo(line(literal(10)))
	}

	@Test
	fun name() {
		script("x")
			.evaluateLine
			.assertEqualTo("x" lineTo script())
	}

	@Test
	fun field_static() {
		script("x" lineTo script("zero"))
			.evaluateLine
			.assertEqualTo("x" lineTo script("zero"))
	}

	@Test
	fun field_single() {
		script("x" lineTo script(literal(10)))
			.evaluateLine
			.assertEqualTo("x" lineTo script(literal(10)))

		script("point" lineTo script("x" lineTo script(literal(10))))
			.evaluateLine
			.assertEqualTo("point" lineTo script("x" lineTo script(literal(10))))
	}

	@Test
	fun structures() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))))
			.evaluateLine
			.assertEqualTo(
				"point" lineTo script(
					"x" lineTo script(literal(10)),
					"y" lineTo script(literal(20))))
	}

	@Test
	fun get() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))),
			"x" lineTo script())
			.evaluateLine
			.assertEqualTo("x" lineTo script(literal(10)))
	}
}