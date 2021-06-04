package leo

import leo.base.assertEqualTo
import leo.natives.minusName
import kotlin.test.Test

class PerformanceTest {
	// Last results: 370ms
	@Test
	fun doRepeatingLong() {
		script(
			line(literal(10000)),
			repeatName lineTo script(
				checkName lineTo script(
					equalName lineTo script(
						line(literal(0)))),
				switchName lineTo script(
					yesName lineTo script(
						numberName lineTo script(),
						endName lineTo script()),
					noName lineTo script(
						numberName lineTo script(),
						minusName lineTo script(line(literal(1))))
					)
				)
			)
			.evaluate
			.assertEqualTo(script(line(literal(0))))
	}
}