package leo.interactive

import leo.base.assertEqualTo
import leo.base.assertSameAfter
import leo.beName
import leo.line
import leo.lineTo
import leo.literal
import leo.plusName
import leo.script
import kotlin.test.Test

class ScriptEvaluateTest {
	@Test
	fun empty() {
		script().assertSameAfter { evaluate }
	}

	@Test
	fun name() {
		script("foo").assertSameAfter { evaluate }
	}

	@Test
	fun text() {
		script(literal("foo")).assertSameAfter { evaluate }
	}

	@Test
	fun be() {
		script(
			"ugly" lineTo script(),
			beName lineTo script("pretty"))
			.evaluate
			.assertEqualTo(script("pretty"))
	}

	@Test
	fun numberPlusNumber() {
		script(
			line(literal(10)),
			plusName lineTo script(literal(20)))
			.evaluate
			.assertEqualTo(script(literal(30)))
	}
}