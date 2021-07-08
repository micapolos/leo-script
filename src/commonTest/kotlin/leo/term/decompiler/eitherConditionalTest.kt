package leo.term.decompiler

import leo.base.assertEqualTo
import leo.base.conditional
import leo.term.eitherFirst
import leo.term.eitherSecond
import leo.term.nativeValue
import kotlin.test.Test

class EitherConditionalTest {
  @Test
  fun eitherFirst() {
    "one".nativeValue
      .eitherFirst
      .eitherConditional
      .assertEqualTo(false.conditional("one".nativeValue))
  }

  @Test
  fun eitherSecond() {
    "one".nativeValue
      .eitherSecond
      .eitherConditional
      .assertEqualTo(true.conditional("one".nativeValue))
  }
}