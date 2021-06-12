package leo.named.evaluator

import leo.base.assertSameAfter
import leo.lineTo
import leo.literal
import leo.script
import kotlin.test.Test

class ScriptEvaluateTest {
	@Test
	fun empty() {
		script().assertSameAfter { evaluate }
	}

	@Test
	fun literals() {
		script(literal(10)).assertSameAfter { evaluate }
		script(literal("foo")).assertSameAfter { evaluate }
	}

	@Test
	fun field() {
		script("x" lineTo script(literal(10))).assertSameAfter { evaluate }
	}

	@Test
	fun fields() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)))
			.assertSameAfter { evaluate }
	}
}