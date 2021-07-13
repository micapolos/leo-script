package leo.term.indexed

import leo.base.assertEqualTo
import leo.empty
import leo.variable
import kotlin.test.Test

class ExpressionValueTest {
  @Test
  fun native() {
    nativeExpression(10)
      .value(incEvaluator)
      .assertEqualTo(nativeValue(11))
  }

  @Test
  fun empty() {
    expression<Int>(empty)
      .value(incEvaluator)
      .assertEqualTo(value(empty))
  }

  @Test
  fun tuple() {
    expression(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(value(nativeValue(11), nativeValue(21)))
  }

  @Test
  fun get() {
    expression(nativeExpression(10), nativeExpression(20))
      .get(0)
      .value(incEvaluator)
      .assertEqualTo(nativeValue(11))

    expression(nativeExpression(10), nativeExpression(20))
      .get(1)
      .value(incEvaluator)
      .assertEqualTo(nativeValue(21))
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
      .assertEqualTo(value(nativeValue(11), nativeValue(21)))
  }

  @Test
  fun index() {
    expression<Int>(10)
      .value(incEvaluator)
      .assertEqualTo(value(10))
  }

  @Test
  fun switch() {
    expression<Int>(0)
      .switch(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(nativeValue(11))

    expression<Int>(1)
      .switch(nativeExpression(10), nativeExpression(20))
      .value(incEvaluator)
      .assertEqualTo(nativeValue(21))
  }

  @Test
  fun indexed() {
    expression(indexed(0, nativeExpression(10)))
      .value(incEvaluator)
      .assertEqualTo(value(indexed(0, nativeValue(11))))
  }

  @Test
  fun indexedSwitch() {
    expression(indexed(0, nativeExpression(100)))
      .indexedSwitch(
        expression(nativeExpression(10), expression(variable(0))),
        expression(nativeExpression(20), expression(variable(0))))
      .value(incEvaluator)
      .assertEqualTo(value(nativeValue(11), nativeValue(101)))

    expression(indexed(1, nativeExpression(100)))
      .indexedSwitch(
        expression(nativeExpression(10), expression(variable(0))),
        expression(nativeExpression(20), expression(variable(0))))
      .value(incEvaluator)
      .assertEqualTo(value(nativeValue(21), nativeValue(101)))
  }
}