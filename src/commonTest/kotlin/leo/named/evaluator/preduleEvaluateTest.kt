package leo.named.evaluator

import leo.base.assertEqualTo
import leo.line
import leo.lineTo
import leo.literal
import leo.plusName
import leo.script
import kotlin.test.Test

class PreludeEvaluateTest {
	@Test
	fun numberPlusNumber() {
		script(
			line(literal(10)),
			plusName lineTo script(literal(20)))
			.evaluate
			.assertEqualTo(script(literal(30)))
	}
}