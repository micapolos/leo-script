package leo.typed.compiler

import leo.base.assertEqualTo
import leo.lineTo
import leo.type
import leo.typed.compiled.compiled
import leo.typed.compiled.expression
import leo.typed.compiled.get
import leo.typed.compiled.variable
import kotlin.test.Test

class BindingTest {
  @Test
  fun multiline_resolveOrNull() {
    binding(
      given(
        type(
          "x" lineTo type("zero"),
          "y" lineTo type("one"))))
      .resolveOrNull(compiled<Nothing>("x"))
      .assertEqualTo(compiled(expression(variable(type("x"))), type("x" lineTo type("zero"))))
  }

  @Test
  fun singleLine_direct_resolveOrNull() {
    binding(
      given(
        type(
          "point" lineTo type(
            "x" lineTo type("zero"),
            "y" lineTo type("one")))))
      .resolveOrNull(compiled<Nothing>("point"))
      .assertEqualTo(
        compiled(
          expression(variable(type("point"))),
          type(
            "point" lineTo type(
              "x" lineTo type("zero"),
              "y" lineTo type("one")))))
  }

  @Test
  fun singleLine_indirect_resolveOrNull() {
    binding(
      given(
        type(
          "point" lineTo type(
            "x" lineTo type("zero"),
            "y" lineTo type("one")))))
      .resolveOrNull(compiled<Nothing>("x"))
      .assertEqualTo(
        compiled(
          expression<Nothing>(variable(type("point"))),
          type(
            "point" lineTo type(
              "x" lineTo type("zero"),
              "y" lineTo type("one")))).get("x"))
  }
}