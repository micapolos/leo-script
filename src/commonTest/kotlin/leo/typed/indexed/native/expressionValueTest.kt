package leo.typed.indexed.native

import leo.base.assertEqualTo
import leo.typed.compiler.native.DoubleIsLessThanDoubleNative
import leo.typed.compiler.native.DoubleMinusDoubleNative
import leo.typed.compiler.native.DoublePlusDoubleNative
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.StringPlusStringNative
import leo.typed.compiler.native.native
import leo.typed.indexed.expression
import leo.typed.indexed.function
import leo.typed.indexed.ifThenElse
import leo.typed.indexed.invoke
import leo.typed.indexed.nativeExpression
import leo.typed.indexed.nativeValue
import leo.typed.indexed.recursive
import leo.typed.indexed.value
import leo.variable
import kotlin.test.Test

class ExpressionValueTest {
  @Test
  fun stringPlusString() {
    nativeExpression(StringPlusStringNative)
      .invoke(
        nativeExpression("Hello, ".native),
        nativeExpression("world!".native))
      .value(nativeEvaluator)
      .assertEqualTo(nativeValue("Hello, world!".native))
  }

  @Test
  fun doubleIsLessThanDouble() {
    nativeExpression(DoubleIsLessThanDoubleNative)
      .invoke(
        nativeExpression(3.0.native),
        nativeExpression(2.0.native))
      .value(nativeEvaluator)
      .assertEqualTo(value(false))

    nativeExpression(DoubleIsLessThanDoubleNative)
      .invoke(
        nativeExpression(1.0.native),
        nativeExpression(2.0.native))
      .value(nativeEvaluator)
      .assertEqualTo(value(true))
  }

  @Test
  fun fibonacci() {
    expression(
      recursive(
        function(1,
          nativeExpression(DoubleIsLessThanDoubleNative)
            .invoke(expression(variable(0)), nativeExpression(2.0.native))
            .ifThenElse(
              expression(variable(0)),
              nativeExpression(DoublePlusDoubleNative)
                .invoke(
                  expression<Native>(variable(1))
                    .invoke(
                      nativeExpression(DoubleMinusDoubleNative)
                        .invoke(
                          expression(variable(0)),
                          nativeExpression(2.0.native))),
                  expression<Native>(variable(1))
                    .invoke(
                      nativeExpression(DoubleMinusDoubleNative)
                        .invoke(
                          expression(variable(0)),
                          nativeExpression(1.0.native))))))))
      .invoke(nativeExpression(10.0.native))
      .value(nativeEvaluator)
      .assertEqualTo(nativeValue(55.0.native))
  }
}