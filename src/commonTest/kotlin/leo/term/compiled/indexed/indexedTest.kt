package leo.term.compiled.indexed

import leo.base.assertEqualTo
import leo.empty
import leo.functionLineTo
import leo.lineTo
import leo.numberName
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
import leo.term.compiled.nativeNumberCompiled
import leo.term.compiled.pick
import leo.term.compiled.switch
import leo.term.compiled.variable
import leo.term.compiler.native.Native
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeNumberType
import leo.term.compiler.native.nativeNumberTypeLine
import leo.term.compiler.native.nativeTextType
import leo.term.compiler.native.nativeTextTypeLine
import leo.term.indexed.expression
import leo.term.indexed.function
import leo.term.indexed.get
import leo.term.indexed.ifThenElse
import leo.term.indexed.indirect
import leo.term.indexed.invoke
import leo.term.indexed.nativeExpression
import leo.term.indexed.switch
import leo.textName
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
  fun function_typeVariable() {
    fn(
      type("x" lineTo type("one"), "y" lineTo type("two")),
      compiled(expression<Nothing>(variable(type("x"))), type("x" lineTo type("one"))))
      .indexedExpression
      .assertEqualTo(expression(function(2, expression(variable(1)))))

    fn(
      type("x" lineTo type("one"), "y" lineTo type("two")),
      compiled(expression<Nothing>(variable(type("y"))), type("y" lineTo type("two"))))
      .indexedExpression
      .assertEqualTo(expression(function(2, expression(variable(0)))))
  }

  @Test
  fun function_variables() {
    fn(
      type(nativeNumberTypeLine, nativeTextTypeLine),
      compiledVariable<String>(type(numberName), nativeNumberType))
      .indexedExpression
      .assertEqualTo(expression(function(2, expression(variable(1)))))

    fn(
      type(nativeNumberTypeLine, nativeTextTypeLine),
      compiledVariable<String>(type(textName), nativeNumberType))
      .indexedExpression
      .assertEqualTo(expression(function(2, expression(variable(0)))))
  }

  @Test
  fun functionInvoke() {
    fn(nativeTextType, compiledVariable<Nothing>(type(textName), nativeNumberType))
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
  fun switchComplexBoolean() {
    compiled(
      "is" lineTo compiledSelect<Native>()
        .pick("yes" lineTo nativeNumberCompiled(10.0.native))
        .drop("no" lineTo nativeNumberType)
        .compiled)
      .switch(
        textType,
        compiledVariable(type("yes"), type("yes" lineTo nativeNumberType)),
        compiledVariable(type("no"), type("no" lineTo nativeNumberType)))
      .indexedExpression
      .assertEqualTo(
        expression(expression(true), nativeExpression(10.0.native))
          .indirect { lhs ->
            lhs
              .get(0)
              .ifThenElse(
                expression(function(1, expression(variable(0)))),
                expression(function(1, expression(variable(0)))))
              .invoke(lhs.get(1))
          })
  }

  @Test
  fun switchComplexIndexed() {
    compiled(
      "is" lineTo compiledSelect<Native>()
        .pick("yes" lineTo nativeNumberCompiled(10.0.native))
        .drop("maybe" lineTo nativeNumberType)
        .drop("no" lineTo nativeNumberType)
        .compiled)
      .switch(
        textType,
        compiledVariable(type("yes"), type("yes" lineTo nativeNumberType)),
        compiledVariable(type("maybe"), type("maybe" lineTo nativeNumberType)),
        compiledVariable(type("no"), type("no" lineTo nativeNumberType)))
      .indexedExpression
      .assertEqualTo(
        expression(expression(0), nativeExpression(10.0.native))
          .indirect { lhs ->
            lhs
              .get(0)
              .switch(
                expression(function(1, expression(variable(0)))),
                expression(function(1, expression(variable(0)))),
                expression(function(1, expression(variable(0)))))
              .invoke(lhs.get(1))
          })
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

  @Test
  fun variable() {
    expression<Unit>(variable(type("one")))
      .indexedExpression(scope(type("one"), type("two")))
      .assertEqualTo(expression(variable(1)))

    expression<Unit>(variable(type("two")))
      .indexedExpression(scope(type("one"), type("two")))
      .assertEqualTo(expression(variable(0)))
  }
}