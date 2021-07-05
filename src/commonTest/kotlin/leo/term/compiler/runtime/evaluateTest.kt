package leo.term.compiler.runtime

import leo.base.assertEqualTo
import leo.base.assertSameAfter
import leo.doName
import leo.getName
import leo.line
import leo.lineTo
import leo.literal
import leo.makeName
import leo.numberName
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
	fun do_() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			doName lineTo script(getName lineTo script("x"))
		)
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
			getName lineTo script("x")
		)
			.evaluate
			.assertEqualTo(script("x" lineTo script(literal(10))))
	}

	@Test
	fun getNumber() {
		script(
			"id" lineTo script(line(literal(10))),
			getName lineTo script(numberName)
		)
			.evaluate
			.assertEqualTo(script(literal(10)))
	}

	@Test
	fun make() {
		script(
			line(literal(10)),
			makeName lineTo script("id")
		)
			.evaluate
			.assertEqualTo(script("id" lineTo script(literal(10))))
	}

	@Test
	fun numberAddNumber() {
		script(
			line(literal(10)),
			"add" lineTo script(literal(20))
		)
			.evaluate
			.assertEqualTo(script(literal(30)))
	}
}