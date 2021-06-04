package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ScriptSyntaxTest {
	@Test
	fun noNormalization() {
		script(
			"foo" lineTo script(),
			"bar" lineTo script())
			.syntax
			.assertEqualTo(syntax("foo" lineTo syntax(), "bar" lineTo syntax()))
	}

	@Test
	fun fields() {
		val script = script(
			"point" lineTo script(
				"x" lineTo script(line(literal(10))),
				"y" lineTo script(line(literal(20)))),
			isName lineTo script(
				equalName lineTo script("foo")),
			"point" lineTo script(
				"x" lineTo script(line(literal(30))),
				"y" lineTo script(line(literal(20)))),
			doName lineTo script("one" lineTo script("two")),
			"point" lineTo script(
				"x" lineTo script(line(literal(50))),
				"y" lineTo script(line(literal(60)))),
			doName lineTo script("three" lineTo script("four")),
		)

		script.syntax.script.assertEqualTo(script)
	}
}