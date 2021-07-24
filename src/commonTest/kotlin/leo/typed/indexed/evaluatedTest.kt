package leo.typed.indexed

import leo.base.assertEqualTo
import leo.typed.compiler.native.native
import leo.typed.indexed.native.nativeEvaluator
import leo.variable
import kotlin.test.Test

class EvaluatedTest {
  @Test
  fun ifThenElse() {
    nativeEvaluator.evaluated
      .set(expression(true))
      .ifThenElse(
        nativeExpression("OK".native),
        nativeExpression("not OK".native))
      .value
      .assertEqualTo(nativeValue("OK".native))
  }

  @Test
  fun bind() {
    nativeEvaluator.evaluated
      .bind(nativeExpression("foo".native))
      .set(expression(variable(0)))
      .value
      .assertEqualTo(nativeValue("foo".native))
  }
}