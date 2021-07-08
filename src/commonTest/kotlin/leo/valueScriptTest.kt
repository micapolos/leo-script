package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ValueScriptTest {
  @Test
  fun literal() {
    value(field(literal("Michał"))).script.assertEqualTo(script(literal("Michał")))
    value(field(literal(123))).script.assertEqualTo(script(literal(123)))
  }

  @Test
  fun doing() {
    value(field(dictionary().function(binder(doing(body(script("foo")))))))
      .script
      .assertEqualTo(script(doingName lineTo script("foo")))
  }

  @Test
  fun applying() {
    value(field(dictionary().function(binder(applying(body(script("foo")))))))
      .script
      .assertEqualTo(script(applyingName lineTo script("foo")))
  }

  @Test
  fun struct() {
    value(
      "point" fieldTo value(
        "x" fieldTo value("zero"),
        "y" fieldTo value("one")
      )
    )
      .script
      .assertEqualTo(
        script(
          "point" lineTo script(
            "x" lineTo script("zero"),
            "y" lineTo script("one")
          )
        )
      )
  }
}