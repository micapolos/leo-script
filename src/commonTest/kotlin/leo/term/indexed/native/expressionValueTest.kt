package leo.term.indexed.native

import leo.base.assertEqualTo
import leo.term.compiler.native.DoubleMinusDoubleNative
import leo.term.compiler.native.Native
import leo.term.compiler.native.ObjectEqualsObjectNative
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
    expression(function(2, nativeExpression(StringPlusStringNative)))
      .invoke(
        nativeExpression("Hello, ".native),
        nativeExpression("world!".native))
      .value(nativeEvaluator)
      .assertEqualTo(nativeValue("Hello, world!".native))
  }

  @Test
  fun recursiveFunction() {
    expression(
      recursive(
        function(1,
          expression(function(2, nativeExpression(ObjectEqualsObjectNative)))
            .invoke(expression(variable(0)), nativeExpression(0.0.native))
            .switch(
              nativeExpression("Hello, world!".native),
              expression<Native>(variable(1))
                .invoke(
                expression(function(2, nativeExpression(DoubleMinusDoubleNative)))
                  .invoke(
                    expression(variable(0)),
                    nativeExpression(1.0.native)))))))
      .invoke(nativeExpression(10.0.native))
      .value(nativeEvaluator)
      .assertEqualTo(nativeValue("Hello, world!".native))
  }
}