package leo

import leo.base.assertEqualTo
import leo.natives.minusName
import kotlin.test.Test

class PerformanceTest {
	// Last results: 380ms
	@Test
	fun doRepeatingLong() {
		script(
			line(literal(10000)),
			doName lineTo script(
				repeatingName lineTo script(
					bindName lineTo script(
						numberName lineTo script(),
						isName lineTo script(equalName lineTo script(line(literal(0)))),
						switchName lineTo script(
							yesName lineTo script(beName lineTo script(line(literal("OK")))),
							noName lineTo script(
								beName lineTo script(
									numberName lineTo script(),
									minusName lineTo script(line(literal(1))),
								),
								repeatName lineTo script()
							)
						)
					)
				)
			)
		)
			.evaluate
			.assertEqualTo(script(line(literal("OK"))))
	}

}