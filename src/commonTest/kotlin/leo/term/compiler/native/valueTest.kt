package leo.term.compiler.native

import leo.base.assertEqualTo
import leo.term.fn
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.nativeValue
import kotlin.test.Test

class EvaluatorTest {
  @Test
  fun double() {
    10.0.native.nativeTerm
      .value
      .assertEqualTo(10.0.native.nativeValue)
  }

  @Test
  fun string() {
    "Hello, world!".native.nativeTerm
      .value
      .assertEqualTo("Hello, world!".native.nativeValue)
  }

  @Test
  fun doubleAddDouble() {
    fn(fn(DoublePlusDoubleNative.nativeTerm))
      .invoke(10.0.native.nativeTerm)
      .invoke(20.0.native.nativeTerm)
      .value
      .assertEqualTo(30.0.native.nativeValue)
  }

  @Test
  fun doubleSubtractDouble() {
    fn(fn(DoubleMinusDoubleNative.nativeTerm))
      .invoke(30.0.native.nativeTerm)
      .invoke(20.0.native.nativeTerm)
      .value
      .assertEqualTo(10.0.native.nativeValue)
  }

  @Test
  fun doubleMultiplyByDouble() {
    fn(fn(DoubleTimesDoubleNative.nativeTerm))
      .invoke(10.0.native.nativeTerm)
      .invoke(20.0.native.nativeTerm)
      .value
      .assertEqualTo(200.0.native.nativeValue)
  }

  @Test
  fun stringAppendString() {
    fn(fn(StringPlusStringNative.nativeTerm))
      .invoke("Hello, ".native.nativeTerm)
      .invoke("world!".native.nativeTerm)
      .value
      .assertEqualTo("Hello, world!".native.nativeValue)
  }
}