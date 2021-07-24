package leo.typed.compiled.indexed

import leo.typed.indexed.expression as indexedExpression
import leo.base.assertEqualTo
import leo.empty
import leo.functionLineTo
import leo.lineTo
import leo.textType
import leo.textTypeLine
import leo.type
import leo.typed.compiled.apply
import leo.typed.compiled.compiled
import leo.typed.compiled.compiledSelect
import leo.typed.compiled.compiledVariable
import leo.typed.compiled.expression
import leo.typed.compiled.fn
import leo.typed.compiled.invoke
import leo.typed.compiled.lineTo
import leo.typed.compiled.linkPlus
import leo.typed.compiled.nativeCompiled
import leo.typed.compiled.nativeCompiledLine
import leo.typed.compiled.nativeNumberCompiled
import leo.typed.compiled.nativeNumberCompiledLine
import leo.typed.compiled.not
import leo.typed.compiled.switch
import leo.typed.compiled.the
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.native
import leo.typed.compiler.native.nativeNumberType
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextType
import leo.typed.compiler.native.nativeTextTypeLine
import leo.typed.indexed.function
import leo.typed.indexed.get
import leo.typed.indexed.ifThenElse
import leo.typed.indexed.indirect
import leo.typed.indexed.invoke
import leo.typed.indexed.nativeExpression
import leo.typed.indexed.switch
import leo.typed.indexed.tuple
import leo.variable
import kotlin.test.Test

class IndexedTest {
  @Test
  fun empty() {
    compiled<Nothing>()
      .indexedExpression
      .assertEqualTo(indexedExpression(empty))
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
      .assertEqualTo(indexedExpression(nativeExpression("foo"), nativeExpression("bar")))
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
      .assertEqualTo(indexedExpression(function(1, nativeExpression("foo"))))
  }

  @Test
  fun function_typeVariable() {
    fn(
      type("x" lineTo type("one"), "y" lineTo type("two")),
      compiled(expression<Nothing>(variable(1)), type("x" lineTo type("one"))))
      .indexedExpression
      .assertEqualTo(indexedExpression(function(2, indexedExpression(variable(1)))))

    fn(
      type("x" lineTo type("one"), "y" lineTo type("two")),
      compiled(expression<Nothing>(variable(0)), type("y" lineTo type("two"))))
      .indexedExpression
      .assertEqualTo(indexedExpression(function(2, indexedExpression(variable(0)))))
  }

  @Test
  fun function_variables() {
    fn(
      type(nativeNumberTypeLine, nativeTextTypeLine),
      compiledVariable<String>(1, nativeNumberType))
      .indexedExpression
      .assertEqualTo(indexedExpression(function(2, indexedExpression(variable(1)))))

    fn(
      type(nativeNumberTypeLine, nativeTextTypeLine),
      compiledVariable<String>(0, nativeNumberType))
      .indexedExpression
      .assertEqualTo(indexedExpression(function(2, indexedExpression(variable(0)))))
  }

  @Test
  fun functionInvoke() {
    fn(nativeTextType, compiledVariable<Nothing>(0, nativeNumberType))
      .invoke(nativeCompiled("foo", nativeTextTypeLine))
      .indexedExpression
      .assertEqualTo(
        indexedExpression(function(1, indexedExpression<String>(variable(0)))).invoke(nativeExpression("foo")))
  }

  @Test
  fun boolean() {
    compiledSelect<String>()
      .the("yes" lineTo compiled())
      .not("no" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(true))

    compiledSelect<String>()
      .not("yes" lineTo type())
      .the("no" lineTo compiled())
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(false))
  }

  @Test
  fun index() {
    compiledSelect<String>()
      .the("yes" lineTo compiled())
      .not("no" lineTo type())
      .not("maybe" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(0))

    compiledSelect<String>()
      .not("yes" lineTo type())
      .the("no" lineTo compiled())
      .not("maybe" lineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(1))

    compiledSelect<String>()
      .not("yes" lineTo type())
      .not("no" lineTo type())
      .the("maybe" lineTo compiled())
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(2))
  }

  @Test
  fun booleanIndexed() {
    compiledSelect<String>()
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .not(nativeTextTypeLine)
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(indexedExpression(true), nativeExpression(10)))

    compiledSelect<String>()
      .not(nativeTextTypeLine)
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(indexedExpression(false), nativeExpression(10)))
  }

  @Test
  fun indexed() {
    compiledSelect<String>()
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .not(nativeTextTypeLine)
      .not(type() functionLineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(indexedExpression(0), nativeExpression(10)))

    compiledSelect<String>()
      .not(nativeTextTypeLine)
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .not(type() functionLineTo type())
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(indexedExpression(1), nativeExpression(10)))

    compiledSelect<String>()
      .not(nativeTextTypeLine)
      .not(type() functionLineTo type())
      .the(nativeCompiledLine(10, nativeNumberTypeLine))
      .compiled
      .indexedExpression
      .assertEqualTo(indexedExpression(indexedExpression(2), nativeExpression(10)))
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
        nativeCompiled("OK", textTypeLine),
        nativeCompiled("fail", textTypeLine))
      .indexedExpression
      .assertEqualTo(
        indexedExpression<String>(true)
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
        nativeCompiled("OK", textTypeLine),
        nativeCompiled("fail", textTypeLine),
        nativeCompiled("maybe", textTypeLine))
      .indexedExpression
      .assertEqualTo(
        indexedExpression<String>(0)
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
        compiledVariable(0, type("yes" lineTo nativeNumberType)),
        compiledVariable(0, type("no" lineTo nativeNumberType)))
      .indexedExpression
      .assertEqualTo(
        indexedExpression(indexedExpression(true), nativeExpression(10.0.native))
          .indirect { lhs ->
            lhs
              .get(0)
              .ifThenElse(
                indexedExpression(function(1, indexedExpression(variable(0)))),
                indexedExpression(function(1, indexedExpression(variable(0)))))
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
        compiledVariable(0, type("yes" lineTo nativeNumberType)),
        compiledVariable(0, type("maybe" lineTo nativeNumberType)),
        compiledVariable(0, type("no" lineTo nativeNumberType)))
      .indexedExpression
      .assertEqualTo(
        indexedExpression(indexedExpression(0), nativeExpression(10.0.native))
          .indirect { lhs ->
            lhs
              .get(0)
              .switch(
                indexedExpression(function(1, indexedExpression(variable(0)))),
                indexedExpression(function(1, indexedExpression(variable(0)))),
                indexedExpression(function(1, indexedExpression(variable(0)))))
              .invoke(lhs.get(1))
          })
  }

  @Test
  fun variable() {
    expression<Unit>(variable(1))
      .indexedExpression
      .assertEqualTo(indexedExpression(variable(1)))

    expression<Unit>(variable(0))
      .indexedExpression
      .assertEqualTo(indexedExpression(variable(0)))
  }

  @Test
  fun empty_plusLine() {
    compiled<Native>()
      .linkPlus(nativeNumberCompiledLine(10.0.native))
      .indexedExpression
      .assertEqualTo(nativeExpression(10.0.native))
  }

  @Test
  fun empty_plusTwoLines() {
    compiled<Native>()
      .linkPlus(nativeNumberCompiledLine(10.0.native))
      .linkPlus(nativeNumberCompiledLine(20.0.native))
      .indexedExpression
      .assertEqualTo(indexedExpression(tuple(nativeExpression(10.0.native), nativeExpression(20.0.native))))
  }

  @Test
  fun line_plusLine() {
    compiled(expression<Native>(variable(0)), nativeNumberType)
      .linkPlus(nativeNumberCompiledLine(10.0.native))
      .indexedExpression
      .assertEqualTo(indexedExpression(tuple(indexedExpression(variable(0)), nativeExpression(10.0.native))))
  }

  @Test
  fun twoLines_plusLine() {
    compiled(expression<Native>(variable(0)), type(nativeNumberTypeLine, nativeTextTypeLine))
      .linkPlus(nativeNumberCompiledLine(10.0.native))
      .indexedExpression
      .assertEqualTo(
        indexedExpression(
          invoke(
            indexedExpression(
              function(
                1,
                indexedExpression(
                  tuple(
                    indexedExpression<Native>(variable(0)).get(0),
                    indexedExpression<Native>(variable(0)).get(1),
                    nativeExpression(10.0.native))))),
            indexedExpression(variable(0)))))
  }

  @Test
  fun invoke() {
    compiled(expression<Native>(variable(128)), type(nativeNumberTypeLine, nativeTextTypeLine))
      .linkPlus(nativeNumberCompiledLine(10.0.native))
      .apply(
        fn(
          type(nativeNumberTypeLine, nativeTextTypeLine, nativeNumberTypeLine),
          compiled("foo")))
      .indexedExpression
      .assertEqualTo(
        indexedExpression(
          invoke(
            indexedExpression(
              function(
                1,
                indexedExpression(
                  invoke(
                    indexedExpression(
                      function(
                        3,
                        indexedExpression(empty))),
                    indexedExpression<Native>(variable(0)).get(0),
                    indexedExpression<Native>(variable(0)).get(1),
                    nativeExpression(10.0.native))))),
            indexedExpression(variable(128)))))
  }
}