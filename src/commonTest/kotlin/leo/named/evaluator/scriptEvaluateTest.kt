package leo.named.evaluator

import leo.base.assertEqualTo
import leo.base.assertSameAfter
import leo.beName
import leo.doName
import leo.doingName
import leo.giveName
import leo.lineTo
import leo.literal
import leo.script
import leo.takeName
import leo.toName
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

	@Test
	fun fieldGet() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))),
			"x" lineTo script())
			.evaluate
			.assertEqualTo(script("x" lineTo script(literal(10))))
	}

	@Test
	fun make() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			"point" lineTo script())
			.evaluate
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script(literal(10)),
						"y" lineTo script(literal(20)))))
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
	fun do_() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			doName lineTo script("x" lineTo script()))
			.evaluate
			.assertEqualTo(script("x" lineTo script(literal(10))))
	}

	@Test
	fun doingGive() {
		script(
			doingName lineTo script(
				"ping" lineTo script(),
				toName lineTo script("pong" lineTo script())),
			giveName lineTo script("ping"))
			.evaluate
			.assertEqualTo(script("pong"))
	}

	@Test
	fun takeDoing() {
		script(
			"ping" lineTo script(),
			takeName lineTo script(
				doingName lineTo script(
					"ping" lineTo script(),
					toName lineTo script("pong" lineTo script()))))
			.evaluate
			.assertEqualTo(script("pong"))
	}
}