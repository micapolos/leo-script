package leo.interactive.parser

import leo.base.assertEqualTo
import leo.lineTo
import leo.literal
import leo.parser.parsed
import leo.script
import kotlin.test.Test

class ScriptParserTest {
	@Test
	fun stringScript() {
		scriptParser
			.parsed("point\n  x 10\n  y 20\n")
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script(literal(10)),
						"y" lineTo script(literal(20))))
			)
	}
}