package leo25

import leo25.base.assertEqualTo
import leo25.natives.minusName
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
						yesName lineTo script(doName lineTo script(line(literal("OK")))),
						noName lineTo script(
							doName lineTo script(
								numberName lineTo script(),
								minusName lineTo script(line(literal(1))),
								repeatName lineTo script(),
							)
						)
					)
				)
			)
		)
			.interpret
			.assertEqualTo(script(line(literal("OK"))))
	}

}