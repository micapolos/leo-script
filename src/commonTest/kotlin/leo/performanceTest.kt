package leo

import leo.base.assertEqualTo
import leo.natives.minusName
import kotlin.test.Test

class PerformanceTest {
  // Last results: 360ms
  @Test
  fun doRepeatingLong() {
    script(
      line(literal(10000)),
      repeatName lineTo script(
        checkName lineTo script(
          equalName lineTo script(
            toName lineTo script(
              line(literal(0))
            )
          )
        ),
        selectName lineTo script(
          yesName lineTo script(
            applyingName lineTo script(
              numberName lineTo script(),
              endName lineTo script()
            )
          ),
          noName lineTo script(
            applyingName lineTo script(
              numberName lineTo script(),
              minusName lineTo script(line(literal(1)))
            )
          )
        )
      )
    )
      .evaluate
      .assertEqualTo(script(line(literal(0))))
  }
}