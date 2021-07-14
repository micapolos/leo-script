package leo.term.compiled.indexed

import leo.base.assertEqualTo
import leo.empty
import leo.lineTo
import leo.numberType
import leo.numberTypeLine
import leo.term.compiled.compiled
import leo.term.compiled.compiledVariable
import leo.term.compiled.drop
import leo.term.compiled.fn
import leo.term.compiled.invoke
import leo.term.compiled.lineTo
import leo.term.compiled.nativeCompiled
import leo.term.compiled.nativeCompiledLine
import leo.term.compiled.pick
import leo.term.indexed.expression
import leo.term.indexed.function
import leo.term.indexed.invoke
import leo.term.indexed.nativeExpression
import leo.textType
import leo.textTypeLine
import leo.type
import leo.variable
import kotlin.test.Test

class IndexedTest {
  @Test
  fun empty() {
    compiled<Nothing>()
      .indexedExpression
      .assertEqualTo(expression(empty))
  }

  @Test
  fun singleLine() {
    compiled(nativeCompiledLine("foo"))
      .indexedExpression
      .assertEqualTo(nativeExpression("foo"))
  }

  @Test
  fun multiLine() {
    compiled(nativeCompiledLine("foo"), nativeCompiledLine("bar"))
      .indexedExpression
      .assertEqualTo(expression(nativeExpression("foo"), nativeExpression("bar")))
  }

  @Test
  fun field() {
    compiled("x" lineTo nativeCompiled("foo"))
      .indexedExpression
      .assertEqualTo(nativeExpression("foo"))
  }

  @Test
  fun function_constant() {
    fn(numberType, nativeCompiled("foo", textTypeLine))
      .indexedExpression
      .assertEqualTo(expression(function(1, nativeExpression("foo"))))
  }

  @Test
  fun function_variable() {
    fn(numberType, compiledVariable<Nothing>(0, numberType))
      .indexedExpression
      .assertEqualTo(expression(function(1, expression(variable(0)))))
  }

  @Test
  fun function_variables() {
    fn(
      type(numberTypeLine, textTypeLine),
      compiledVariable<String>(0, numberType))
      .indexedExpression
      .assertEqualTo(expression(function(2, expression(variable(0)))))

    fn(
      type(numberTypeLine, textTypeLine),
      compiledVariable<String>(1, numberType))
      .indexedExpression
      .assertEqualTo(expression(function(2, expression(variable(1)))))
  }

  @Test
  fun functionInvoke() {
    fn(textType, compiledVariable<Nothing>(0, numberType))
      .invoke(nativeCompiled("foo", textTypeLine))
      .indexedExpression
      .assertEqualTo(expression(function(1, expression<String>(variable(0)))).invoke(nativeExpression("foo")))
  }

  @Test
  fun boolean() {
    compiled(
      "is" lineTo compiled<String>()
        .pick("yes" lineTo compiled())
        .drop("no" lineTo type()))
      .indexedExpression
      .assertEqualTo(expression(true))
  }
}