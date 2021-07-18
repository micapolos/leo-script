package leo.term.compiled.indexed

import leo.base.assertEqualTo
import leo.empty
import leo.functionLineTo
import leo.lineTo
import leo.term.compiled.bind
import leo.term.compiled.binding
import leo.term.compiled.compiled
import leo.term.compiled.compiledSelect
import leo.term.compiled.compiledVariable
import leo.term.compiled.drop
import leo.term.compiled.expression
import leo.term.compiled.fn
import leo.term.compiled.invoke
import leo.term.compiled.lineTo
import leo.term.compiled.nativeCompiled
import leo.term.compiled.nativeCompiledLine
import leo.term.compiled.pick
import leo.term.compiled.switch
import leo.term.compiled.variable
import leo.term.compiler.native.nativeNumberType
import leo.term.compiler.native.nativeNumberTypeLine
import leo.term.compiler.native.nativeTextType
import leo.term.compiler.native.nativeTextTypeLine
import leo.term.indexed.expression
import leo.term.indexed.function
import leo.term.indexed.ifThenElse
import leo.term.indexed.invoke
import leo.term.indexed.nativeExpression
import leo.term.indexed.switch
import leo.textType
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
    fn(nativeNumberType, nativeCompiled("foo", nativeTextTypeLine))
      .indexedExpression
      .assertEqualTo(expression(function(1, nativeExpression("foo"))))
  }

  @Test
  fun function_variable() {
    fn(nativeNumberType, compiledVariable<Nothing>(0, nativeNumberType))
      .indexedExpression
      .assertEqualTo(expression(function(1, expression(variable(0)))))
  }

  @Test
  fun function_variables() {
    fn(
      type(nativeNumberTypeLine, nativeTextTypeLine),
      compiledVariable<String>(0, nativeNumberType))
      .indexedExpression
      .assertEqualTo(expression(function(2, expression(variable(0)))))

    fn(
      type(nativeNumberTypeLine, nativeTextTypeLine),
      compiledVariable<String>(1, nativeNumberType))
      .indexedExpression
      .assertEqualTo(expression(function(2, expression(variable(1)))))
  }

  @Test
  fun functionInvoke() {
    fn(nativeTextType, compiledVariable<Nothing>(0, nativeNumberType))
      .invoke(nativeCompiled("foo", nativeTextTypeLine))
      .indexedExpression
      .assertEqualTo(expression(function(1, expression<String>(variable(0)))).invoke(nativeExpression("foo")))
  }

  @Test
  fun boolean() {
    compiledSelect<String>()
      .pick("yes" lineTo compiled())
      .drop("no" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(true))

    compiledSelect<String>()
      .drop("yes" lineTo type())
      .pick("no" lineTo compiled())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(false))
  }

  @Test
  fun index() {
    compiledSelect<String>()
      .pick("yes" lineTo compiled())
      .drop("no" lineTo type())
      .drop("maybe" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(0))

    compiledSelect<String>()
      .drop("yes" lineTo type())
      .pick("no" lineTo compiled())
      .drop("maybe" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(1))

    compiledSelect<String>()
      .drop("yes" lineTo type())
      .drop("no" lineTo type())
      .pick("maybe" lineTo compiled())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(2))
  }

  @Test
  fun booleanIndexed() {
    compiledSelect<String>()
      .pick(nativeCompiledLine(10, nativeNumberTypeLine))
      .drop(nativeTextTypeLine)
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(true), nativeExpression(10)))

    compiledSelect<String>()
      .drop(nativeTextTypeLine)
      .pick(nativeCompiledLine(10, nativeNumberTypeLine))
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(false), nativeExpression(10)))
  }

  @Test
  fun indexed() {
    compiledSelect<String>()
      .pick(nativeCompiledLine(10, nativeNumberTypeLine))
      .drop(nativeTextTypeLine)
      .drop(type() functionLineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(0), nativeExpression(10)))

    compiledSelect<String>()
      .drop(nativeTextTypeLine)
      .pick(nativeCompiledLine(10, nativeNumberTypeLine))
      .drop(type() functionLineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(1), nativeExpression(10)))

    compiledSelect<String>()
      .drop(nativeTextTypeLine)
      .drop(type() functionLineTo type())
      .pick(nativeCompiledLine(10, nativeNumberTypeLine))
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(2), nativeExpression(10)))
  }

  @Test
  fun switchSimpleBoolean() {
    compiled(
      "is" lineTo compiledSelect<String>()
        .pick("yes" lineTo compiled())
        .drop("no" lineTo type())
        .compiled)
      .switch(
        textType,
        nativeCompiled("OK", textType),
        nativeCompiled("fail", textType))
      .indexedExpression
      .assertEqualTo(
        expression<String>(true)
          .ifThenElse(
            nativeExpression("OK"),
            nativeExpression("fail")))
  }

  @Test
  fun switchSimpleIndex() {
    compiled(
      "is" lineTo compiledSelect<String>()
        .pick("yes" lineTo compiled())
        .drop("no" lineTo type())
        .drop("maybe" lineTo type())
        .compiled)
      .switch(
        textType,
        nativeCompiled("OK", textType),
        nativeCompiled("fail", textType),
        nativeCompiled("maybe", textType))
      .indexedExpression
      .assertEqualTo(
        expression<String>(0)
          .switch(
            nativeExpression("OK"),
            nativeExpression("fail"),
            nativeExpression("maybe")))
  }

  @Test
  fun bind() {
    compiled(
      expression(
        bind(
          binding(type("foo"), nativeCompiled("bar")),
          compiled(expression(variable(type("foo"))), type("bar")))),
      type("bar"))
      .indexedExpression(scope())
      .assertEqualTo(expression(function(1, expression<String>(variable(0)))).invoke(nativeExpression("bar")))
  }

//  @Test
//  fun switchComplex() {
//    compiled(
//      "is" lineTo compiledSelect<String>()
//        .pick("yes" lineTo nativeCompiled("foo", textType))
//        .drop("no" lineTo textType)
//        .compiled)
//      .switch(
//        textType,
//        compiled<String>(expression(leo.term.variable(0)), type("yes" lineTo textType)).getOrNull(textName)!!,
//        compiled<String>(expression(leo.term.variable(0)), type("no" lineTo textType)).getOrNull(textName)!!)
//      .indexedExpression
//      .assertEqualTo(
//        expression<String>(true)
//          .switch(
//            nativeExpression("OK"),
//            nativeExpression("fail")))
//  }
}