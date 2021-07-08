package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeResolutionTest {
  @Test
  fun get() {
    type(
      "x" lineTo type(
        "point" lineTo type(
          "x" lineTo type(numberTypeLine),
          "y" lineTo type(numberTypeLine)
        )
      )
    )
      .resolveGetOrNull
      .assertEqualTo(type("x" lineTo type(numberTypeLine)))
  }
}