package leo.term.indexed.native

import leo.base.assertEqualTo
import leo.term.compiler.native.DoubleIsLessThanDoubleNative
import leo.term.compiler.native.DoubleMinusDoubleNative
import leo.term.compiler.native.DoublePlusDoubleNative
import leo.term.compiler.native.Native
import leo.term.compiler.native.StringPlusStringNative
import leo.term.compiler.native.native
import leo.term.indexed.expression
import leo.term.indexed.function
import leo.term.indexed.invoke
import leo.term.indexed.nativeExpression
import leo.term.indexed.nativeValue
import leo.term.indexed.recursive
import leo.term.indexed.switch
import leo.term.indexed.value
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
      .assertEqualTo(value(false.switchIndex))

    nativeExpression(DoubleIsLessThanDoubleNative)
      .invoke(
        nativeExpression(1.0.native),
        nativeExpression(2.0.native))
      .value(nativeEvaluator)
      .assertEqualTo(value(true.switchIndex))
  }

  @Test
  fun fibonacci() {
    expression(
      recursive(
        function(1,
          nativeExpression(DoubleIsLessThanDoubleNative)
            .invoke(expression(variable(0)), nativeExpression(2.0.native))
            .switch(
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