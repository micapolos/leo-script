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
  fun pair() {
    scheme("a").plus(scheme("b"))
      .string
      .assertEqualTo("`(a . b)")
  }

  @Test
  fun list() {
    listScheme(scheme("a"), scheme("b"), scheme("c"))
      .string
      .assertEqualTo("`(a b c)")
  }

  @Test
  fun vector() {
    vectorScheme(scheme("a"), scheme("b"), scheme("c"))
      .string
      .assertEqualTo("#(a b c)")
  }

  @Test
  fun value() {
    valueScheme().string.assertEqualTo("`()")
    valueScheme(scheme("a")).string.assertEqualTo("a")
    valueScheme(scheme("a"), scheme("b")).string.assertEqualTo("`(a . b)")
    valueScheme(scheme("a"), scheme("b"), scheme("c")).string.assertEqualTo("#(a b c)")
  }
}