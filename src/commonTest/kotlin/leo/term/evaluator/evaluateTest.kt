package leo.term.evaluator

import leo.actionName
import leo.anyName
import leo.base.assertEqualTo
import leo.base.assertSameAfter
import leo.doingName
import leo.giveName
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.numberName
import leo.performName
import leo.plusName
import leo.script
import kotlin.test.Test

class EvaluateTest {
	@Test
	fun lines() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)))
			.assertSameAfter { evaluate }
	}

	@Test
	fun moreLines() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			"z" lineTo script(literal(30)))
			.assertSameAfter { evaluate }
	}

	@Test
	fun give() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			giveName lineTo script("x"))
			.evaluate
			.assertEqualTo(script("x" lineTo script(literal(10))))
	}

	@Test
	fun get() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))
			),
			"x" lineTo script())
			.evaluate
			.assertEqualTo(script("x" lineTo script(literal(10))))
	}

	@Test
	fun getNumber() {
		script(
			"id" lineTo script(line(literal(10))),
			numberName lineTo script())
			.evaluate
			.assertEqualTo(script(literal(10)))
	}

	@Test
	fun make() {
		script(
			line(literal(10)),
			"id" lineTo script())
			.evaluate
			.assertEqualTo(script("id" lineTo script(literal(10))))
	}

	@Test
	fun numberPlusNumber() {
		script(
			line(literal(10)),
			plusName lineTo script(literal(20))
		)
			.evaluate
			.assertEqualTo(script(literal(30)))
	}

	@Test
	fun performAction() {
		script(
			line(literal(10)),
			performName lineTo script(
				actionName lineTo script(
					anyName lineTo script(numberName),
					doingName lineTo script(
						numberName lineTo script(),
						plusName lineTo script(literal(1))))))
			.evaluate
			.assertEqualTo(script(literal(11)))
	}

	@Test
	fun remember() {
		script(
			letName lineTo script(
				"ping" lineTo script(),
				giveName lineTo script("pong")),
			"ping" lineTo script())
			.evaluate
			.assertEqualTo(script("pong"))
	}
}