package leo.interactive.parser

import leo.base.assertEqualTo
import leo.lineTo
import leo.literal
import leo.parser.parsed
import leo.script
import kotlin.test.Test

class ScriptParserTest {
  @Test
  fun structure() {
    scriptParser
      .parsed("point\n  x 10\n  y 20\n")
      .assertEqualTo(
        script(
          "point" lineTo script(
            "x" lineTo script(literal(10)),
            "y" lineTo script(literal(20))
          )
        )
      )
  }

  // TODO: fixit
//	@Test
//	fun deeperStructure() {
//		scriptParser
//			.parsed("circle\n  radius 10\n  center point\n    x 10\n    y 20\n")
//			.assertEqualTo(
//				script(
//					"circle" lineTo script(
//						"radius" lineTo script(literal(10)),
//						"center" lineTo script(
//							"point" lineTo script(
//								"x" lineTo script(literal(10)),
//								"y" lineTo script(literal(20)))))))
//	}
}