package leo

import leo.base.assertEqualTo
import leo.natives.minusName
import kotlin.test.Test

class ScriptSyntaxTest {
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
			doName lineTo script(line("one"), line("two")),
			"point" lineTo script(
				"x" lineTo script(line(literal(50))),
				"y" lineTo script(line(literal(60)))),
			doName lineTo script(line("three"), line("four")),
		)

		script.syntax.script.assertEqualTo(script)
	}

	@Test
	fun repeating() {
		val script = script(
			line(literal(10000)),
			doName lineTo script(
				repeatingName lineTo script(
					numberName lineTo script(),
					isName lineTo script(equalName lineTo script(line(literal(0)))),
					switchName lineTo script(
						yesName lineTo script(doingName lineTo script(line(literal("OK")))),
						noName lineTo script(
							doingName lineTo script(
								numberName lineTo script(),
								minusName lineTo script(line(literal(1))),
								repeatName lineTo script(),
							)
						)
					)
				)))

		script.syntax.script.assertEqualTo(script)
	}
}