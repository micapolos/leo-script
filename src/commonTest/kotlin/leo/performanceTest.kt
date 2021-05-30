package leo

import leo.base.assertEqualTo
import leo.natives.minusName
import kotlin.test.Test

class PerformanceTest {
	// Last results
	// - jvm: 630ms
	// - maxosX64 - 4950ms
	@Test
	fun doRepeatingLong() {
		script(
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
				)
			)
		)
			.evaluate
			.assertEqualTo(script(line(literal("OK"))))
	}

}