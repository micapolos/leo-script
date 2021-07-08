package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class NumberTest {
  @Test
  fun string() {
    number(123).string.assertEqualTo("123")
    number(123.0).string.assertEqualTo("123")
    number(123.1).string.assertEqualTo("123.1")
    number(-123.1).string.assertEqualTo("-123.1")
    number(0).string.assertEqualTo("0")
    number(-0).string.assertEqualTo("0")
  }
}