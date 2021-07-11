package scheme

import leo.base.assertEqualTo
import kotlin.test.Test

class SchemeStringTest {
  @Test
  fun nil() {
    nilScheme
      .string
      .assertEqualTo("`()")
  }

  @Test
  fun list() {
    listScheme(scheme("a"), scheme("b"), scheme("c"))
      .string
      .assertEqualTo("(list a b c)")
  }

  @Test
  fun vector() {
    vectorScheme(scheme("a"), scheme("b"), scheme("c"))
      .string
      .assertEqualTo("(vector a b c)")
  }

  @Test
  fun tuple() {
    tupleScheme().string.assertEqualTo("`()")
    tupleScheme(scheme("a")).string.assertEqualTo("a")
    tupleScheme(scheme("a"), scheme("b")).string.assertEqualTo("(vector a b)")
  }

  @Test
  fun indexSwitch() {
    scheme("x")
      .indexSwitch(scheme("a"), scheme("b"), scheme("c"))
      .string
      .assertEqualTo("(case x (0 a) (1 b) (2 c))")
  }
}