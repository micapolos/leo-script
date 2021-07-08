package leo.term

import leo.base.assertEqualTo
import kotlin.test.Test

class EvaluateTest {
  @Test
  fun eitherFirst() {
    "foo".nativeTerm
      .eitherFirst
      .value(evaluator())
      .assertEqualTo("foo".nativeValue.eitherFirst)
  }

  @Test
  fun eitherSecond() {
    "foo".nativeTerm
      .eitherSecond
      .value(evaluator())
      .assertEqualTo("foo".nativeValue.eitherSecond)
  }

  @Test
  fun switch_first() {
    "foo".nativeTerm.eitherFirst
      .invoke(id())
      .invoke(id())
      .value(evaluator())
      .assertEqualTo("foo".nativeValue)

    "foo".nativeTerm.eitherFirst
      .invoke(fn("one".nativeTerm))
      .invoke(fn("two".nativeTerm))
      .value(evaluator())
      .assertEqualTo("one".nativeValue)
  }

  @Test
  fun switch_second() {
    "foo".nativeTerm.eitherSecond
      .invoke(id())
      .invoke(id())
      .value(evaluator())
      .assertEqualTo("foo".nativeValue)

    "foo".nativeTerm.eitherSecond
      .invoke(fn("one".nativeTerm))
      .invoke(fn("two".nativeTerm))
      .value(evaluator())
      .assertEqualTo("two".nativeValue)
  }
}