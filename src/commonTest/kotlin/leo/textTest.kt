package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TextTest {
  @Test
  fun string() {
    text("Hello, world!").string.assertEqualTo("Hello, world!")
    text("Hello, ", "world!").string.assertEqualTo("Hello, world!")
    text("Hello, ").plus(text("world!")).string.assertEqualTo("Hello, world!")
  }
}