package leo.indexed.evaluator

import leo.base.assertEqualTo
import leo.beName
import leo.doName
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.numberName
import leo.script
import leo.type.compiler.numberTypeScriptLine
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

	@Test
	fun do_() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			doName lineTo script("x"))
			.evaluate
			.assertEqualTo(10.0)
	}

	@Test
	fun letBe() {
		script(
			letName lineTo script(
				"x" lineTo script(),
				beName lineTo script(literal(10))),
			letName lineTo script(
				"y" lineTo script(),
				beName lineTo script(literal(20))),
			"x" lineTo script())
			.evaluate
			.assertEqualTo(10.0)
	}

	@Test
	fun letDo() {
		script(
			letName lineTo script(
				"length" lineTo script(numberTypeScriptLine),
				doName lineTo script(line("length"), line(numberName))),
			"length" lineTo script(literal(10)))
			.evaluate
			.assertEqualTo(10.0)
	}
}