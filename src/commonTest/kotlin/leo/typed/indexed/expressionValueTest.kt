package leo.typed.indexed

import leo.base.assertEqualTo
import leo.empty
import leo.variable
import kotlin.test.Test

class ExpressionValueTest {
  @Test
  fun native() {
    nativeExpression(10)
      .value(incEvaluator)
      .assertEqualTo(nativeValue(10))
  }

  @Test
  fun empty() {
    expression<Int>(empty)
      .value(incEvaluator)
      .assertEqualTo(value(empty))
  }

  @Test
  fun boolean() {
    expression<Int>(true)
      .value(incEvaluator)
      .assertEqualTo(value(true))

    expression<Int>(false)
      .value(incEvaluator)
      .assertEqualTo(value(false))
  }

  @Test
  fun ifThenElse() {
    expression<Int>(true)
      .ifThenElse(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(nativeValue(10))

    expression<Int>(false)
      .ifThenElse(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(nativeValue(20))
  }

  @Test
  fun tuple() {
    expression(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(value(nativeValue(10), nativeValue(20)))
  }

  @Test
  fun get() {
    expression(nativeExpression(10), nativeExpression(20))
      .get(0)
      .value(incEvaluator)
      .assertEqualTo(nativeValue(10))

    expression(nativeExpression(10), nativeExpression(20))
      .get(1)
      .value(incEvaluator)
      .assertEqualTo(nativeValue(20))
  }

  @Test
  fun function() {
    expression<Int>(function(2, expression(expression(variable(1)), expression(variable(0)))))
      .value(incEvaluator)
      .assertEqualTo(value(function(scope(), expression(expression(variable(1)), expression(variable(0))))))
  }

  @Test
  fun invoke() {
    expression<Int>(function(2, expression(expression(variable(1)), expression(variable(0)))))
      .invoke(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(value(nativeValue(10), nativeValue(20)))
  }

  @Test
  fun nativeInvoke() {
    nativeExpression(100).invoke(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(nativeValue(102))
  }

  @Test
  fun index_boolean() {
    expression<Int>(0)
      .value(incEvaluator)
      .assertEqualTo(value(0))

    expression<Int>(1)
      .value(incEvaluator)
      .assertEqualTo(value(1))
  }

  @Test
  fun switch() {
    expression<Int>(0)
      .switch(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(nativeValue(10))

    expression<Int>(1)
      .switch(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(nativeValue(20))
  }
}