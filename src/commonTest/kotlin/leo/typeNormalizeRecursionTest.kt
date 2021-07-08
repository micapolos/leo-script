package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeNormalizeRecursionTest {
  @Test
  fun normalizeRecursion() {
    type(
      "foo" lineTo type(
        "bar" lineTo type(
          recursiveLine(
            "foo" lineTo type(
              "bar" lineTo type(recurseTypeLine)
            )
          )
        )
      )
    )
      .normalizeRecursion
      .assertEqualTo(
        type(
          recursiveLine(
            "foo" lineTo type(
              "bar" lineTo type(recurseTypeLine)
            )
          )
        )
      )
  }
}
