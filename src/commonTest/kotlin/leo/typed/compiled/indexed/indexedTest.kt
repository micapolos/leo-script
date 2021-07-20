package leo.typed.compiled.indexed

import leo.base.assertEqualTo
import leo.empty
import leo.functionLineTo
import leo.lineTo
import leo.numberName
import leo.textName
import leo.textType
import leo.type
import leo.typed.compiled.bind
import leo.typed.compiled.binding
import leo.typed.compiled.compiled
import leo.typed.compiled.compiledSelect
import leo.typed.compiled.compiledVariable
import leo.typed.compiled.expression
import leo.typed.compiled.fn
import leo.typed.compiled.invoke
import leo.typed.compiled.lineTo
import leo.typed.compiled.nativeCompiled
import leo.typed.compiled.nativeCompiledLine
import leo.typed.compiled.nativeNumberCompiled
import leo.typed.compiled.not
import leo.typed.compiled.switch
import leo.typed.compiled.the
import leo.typed.compiled.variable
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.native
import leo.typed.compiler.native.nativeNumberType
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextType
import leo.typed.compiler.native.nativeTextTypeLine
import leo.typed.indexed.expression
import leo.typed.indexed.function
import leo.typed.indexed.get
import leo.typed.indexed.ifThenElse
import leo.typed.indexed.indirect
import leo.typed.indexed.invoke
import leo.typed.indexed.nativeExpression
import leo.typed.indexed.switch
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
      .the("yes" lineTo compiled())
      .not("no" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(true))

    compiledSelect<String>()
      .not("yes" lineTo type())
      .the("no" lineTo compiled())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(false))
  }

  @Test
  fun index() {
    compiledSelect<String>()
      .the("yes" lineTo compiled())
      .not("no" lineTo type())
      .not("maybe" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(0))

    compiledSelect<String>()
      .not("yes" lineTo type())
      .the("no" lineTo compiled())
      .not("maybe" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(1))

    compiledSelect<String>()
      .not("yes" lineTo type())
      .not("no" lineTo type())
      .the("maybe" lineTo compiled())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(2))
  }

  @Test
  fun booleanIndexed() {
    compiledSelect<String>()
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .not(nativeTextTypeLine)
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(true), nativeExpression(10)))

    compiledSelect<String>()
      .not(nativeTextTypeLine)
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(false), nativeExpression(10)))
  }

  @Test
  fun indexed() {
    compiledSelect<String>()
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .not(nativeTextTypeLine)
      .not(type() functionLineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(0), nativeExpression(10)))

    compiledSelect<String>()
      .not(nativeTextTypeLine)
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .not(type() functionLineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(1), nativeExpression(10)))

    compiledSelect<String>()
      .not(nativeTextTypeLine)
      .not(type() functionLineTo type())
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .compiled
      .indexedExpression
      .assertEqualTo(expression(expression(2), nativeExpression(10)))
  }

  @Test
  fun switchSimpleBoolean() {
    compiled(
      "is" lineTo compiledSelect<String>()
        .the("yes" lineTo compiled())
        .not("no" lineTo type())
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
        .the("yes" lineTo compiled())
        .not("no" lineTo type())
        .not("maybe" lineTo type())
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
        .the("yes" lineTo nativeNumberCompiled(10.0.native))
        .not("no" lineTo nativeNumberType)
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
        .the("yes" lineTo nativeNumberCompiled(10.0.native))
        .not("maybe" lineTo nativeNumberType)
        .not("no" lineTo nativeNumberType)
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