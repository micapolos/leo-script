package leo.term.compiler

import leo.anyTextScriptLine
import leo.applyName
import leo.asName
import leo.atom
import leo.base.assertEqualTo
import leo.choice
import leo.doName
import leo.doingName
import leo.dropName
import leo.function
import leo.functionLineTo
import leo.functionName
import leo.getName
import leo.givingName
import leo.line
import leo.lineTo
import leo.literal
import leo.numberName
import leo.pickName
import leo.plusName
import leo.quoteName
import leo.repeatName
import leo.script
import leo.switchName
import leo.term.compiled.apply
import leo.term.compiled.body
import leo.term.compiled.compiled
import leo.term.compiled.compiledSelect
import leo.term.compiled.drop
import leo.term.compiled.expression
import leo.term.compiled.field
import leo.term.compiled.fn
import leo.term.compiled.get
import leo.term.compiled.indexed
import leo.term.compiled.line
import leo.term.compiled.lineTo
import leo.term.compiled.nativeCompiled
import leo.term.compiled.nativeLine
import leo.term.compiled.nativeNumberCompiled
import leo.term.compiled.nativeNumberCompiledLine
import leo.term.compiled.pick
import leo.term.compiled.recursive
import leo.term.compiled.select
import leo.term.compiled.switch
import leo.term.compiler.native.DoublePlusDoubleNative
import leo.term.compiler.native.Native
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeCompiler
import leo.term.compiler.native.nativeEnvironment
import leo.term.compiler.native.nativeNumberType
import leo.term.compiler.native.nativeNumberTypeLine
import leo.term.compiler.native.nativeTextTypeLine
import leo.term.variable
import leo.textName
import leo.type
import leo.typeName
import kotlin.test.Test
import kotlin.test.assertFails

class CompileTest {
  @Test
  fun empty() {
    nativeEnvironment
      .compiled(script())
      .assertEqualTo(compiled())
  }

  @Test
  fun name() {
    nativeEnvironment
      .compiled(script("foo"))
      .assertEqualTo(compiled("foo" lineTo compiled()))
  }

  @Test
  fun field() {
    nativeEnvironment
      .compiled(script("foo" lineTo script("bar")))
      .assertEqualTo(compiled("foo" lineTo compiled("bar" lineTo compiled())))
  }

  @Test
  fun number() {
    nativeEnvironment
      .compiled(script(literal(10)))
      .assertEqualTo(compiled(compiled(nativeLine(10.0.native), nativeNumberTypeLine)))
  }

  @Test
  fun text() {
    nativeEnvironment
      .compiled(script(literal("foo")))
      .assertEqualTo(compiled(compiled(nativeLine("foo".native), nativeTextTypeLine)))
  }

  @Test
  fun names() {
    nativeEnvironment
      .compiled(script("foo", "bar"))
      .assertEqualTo(compiled("bar" lineTo compiled("foo")))
  }

  @Test
  fun fields() {
    nativeEnvironment
      .compiled(
        script(
          "x" lineTo script("zero"),
          "y" lineTo script("one")
        )
      )
      .assertEqualTo(
        compiled(
          "x" lineTo compiled("zero" lineTo compiled()),
          "y" lineTo compiled("one" lineTo compiled())
        )
      )
  }

  @Test
  fun get() {
    nativeEnvironment
      .compiled(
        script(
          "point" lineTo script(
            "x" lineTo script("zero"),
            "y" lineTo script("one")
          ),
          "x" lineTo script()
        )
      )
      .assertEqualTo(
        compiled(
          "x" lineTo compiled("zero" lineTo compiled())
        )
      )
  }

  @Test
  fun do_constant() {
    script(
      line(literal(10)),
      doName lineTo script("ok"))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        nativeNumberCompiled(10.0.native)
          .apply(fn(nativeNumberType, compiled("ok")))
      )
  }

  @Test
  fun repeat_constant() {
    script(
      line(literal(10)),
      repeatName lineTo script(
        givingName lineTo script("ok"),
        doingName lineTo script("ok")))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        nativeNumberCompiled(10.0.native)
          .apply(fn(nativeNumberType, recursive(body(compiled("ok")))))
      )
  }

  @Test
  fun repeat_variable() {
    script(
      line(literal(10)),
      repeatName lineTo script(
        givingName lineTo nativeNumberType.script,
        doingName lineTo script("number")))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        nativeNumberCompiled(10.0.native)
          .apply(fn(nativeNumberType, recursive(body(compiled(expression(variable(0)), nativeNumberType))))))
  }

  @Test
  fun repeat_recursion() {
    script(
      "ping" lineTo script("pong"),
      repeatName lineTo script(
        givingName lineTo script("ping" lineTo script("pong")),
        doingName lineTo script("ping" lineTo script("pong"))))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        compiled<Native>("ping" lineTo compiled("pong"))
          .apply(
            fn(
              type("ping" lineTo type("pong")),
              recursive(
                body(
                  compiled<Native>("ping" lineTo compiled("pong"))
                    .apply(
                      compiled(
                        expression(variable(1)),
                        type(line(atom(
                          function(
                            type("ping" lineTo type("pong")),
                            type("ping" lineTo type("pong")))))))))))))
  }

  @Test
  fun do_variable() {
    nativeCompiler
      .plus(
        script(
          line(literal(10)),
          doName lineTo script(numberName)))
      .assertEqualTo(
        nativeCompiler
          .plus(nativeNumberCompiledLine(10.0.native))
          .do_(body(compiled(expression(variable(0)), nativeNumberType))))
  }

  @Test
  fun numberPlusNumber() {
    nativeEnvironment
      .compiled(
        script(
          line(literal(10)),
          plusName lineTo script(literal(20))
        )
      )
      .assertEqualTo(
        compiled(
          nativeNumberCompiledLine(10.0.native),
          "plus" lineTo nativeNumberCompiled(20.0.native))
          .apply(
            nativeCompiled(
              DoublePlusDoubleNative,
              type(type(nativeNumberTypeLine, "plus" lineTo nativeNumberType) functionLineTo nativeNumberType))))
  }

  @Test
  fun function() {
    nativeEnvironment
      .compiled(
        script(
          functionName lineTo script(
            "zero" lineTo script(),
            doingName lineTo script("one")
          )
        )
      )
      .assertEqualTo(fn(type("zero"), compiled("one" lineTo compiled())))
  }

  @Test
  fun apply() {
    nativeEnvironment
      .compiled(
        script(
          "ping" lineTo script(),
          applyName lineTo script(
            functionName lineTo script(
              "ping" lineTo script(),
              doingName lineTo script("pong")
            )
          )
        )
      )
      .assertEqualTo(compiled<Native>("ping").apply(fn(type("ping"), compiled("pong"))))
  }

  @Test
  fun quote() {
    nativeEnvironment
      .compiled(
        script(quoteName lineTo script(getName lineTo script("foo")))
      )
      .assertEqualTo(nativeEnvironment.staticCompiled(script(getName lineTo script("foo"))))
  }

  @Test
  fun pickDrop() {
    nativeEnvironment
      .compiled(
        script(
          pickName lineTo script(literal(10)),
          dropName lineTo script(textName)
        )
      )
      .assertEqualTo(
        compiledSelect<Native>()
          .pick(compiled(nativeLine(10.0.native), nativeNumberTypeLine))
          .drop(nativeTextTypeLine)
          .compiled)
  }

  @Test
  fun switch_simple() {
    nativeEnvironment
      .compiled(
        script(
          "id" lineTo script(
            pickName lineTo script("one"),
            dropName lineTo script("two"),
            dropName lineTo script("three")),
          switchName lineTo script(
            "one" lineTo script(doingName lineTo script(literal(1))),
            "two" lineTo script(doingName lineTo script(literal(2))),
            "three" lineTo script(doingName lineTo script(literal(3))))))
      .assertEqualTo(
        compiled(
          "id" lineTo
              compiledSelect<Native>()
                .pick("one" lineTo compiled())
                .drop("two" lineTo type())
                .drop("three" lineTo type())
                .compiled)
          .switch(
            nativeNumberType,
            nativeNumberCompiled(1.0.native),
            nativeNumberCompiled(2.0.native),
            nativeNumberCompiled(3.0.native)))
  }

  @Test
  fun switch_complex() {
    nativeEnvironment
      .compiled(
        script(
          "id" lineTo script(
            pickName lineTo script("one" lineTo script(literal(10))),
            dropName lineTo script("two" lineTo script(numberName)),
            dropName lineTo script("three" lineTo script(numberName))),
          switchName lineTo script(
            "one" lineTo script(doingName lineTo script("one", "number")),
            "two" lineTo script(doingName lineTo script("two", "number")),
            "three" lineTo script(doingName lineTo script("three", "number"))
          )
        )
      )
      .assertEqualTo(
        compiled(
          "id" lineTo
            compiledSelect<Native>()
              .pick("one" lineTo nativeNumberCompiled(10.0.native))
              .drop("two" lineTo nativeNumberType)
              .drop("three" lineTo nativeNumberType)
              .compiled)
          .switch(
            nativeNumberType,
            compiled(expression<Native>(variable(0)), type("one" lineTo nativeNumberType)).get(numberName),
            compiled(expression<Native>(variable(0)), type("two" lineTo nativeNumberType)).get(numberName),
            compiled(expression<Native>(variable(0)), type("three" lineTo nativeNumberType)).get(numberName)))
  }

  @Test
  fun type_() {
    script(
      "point" lineTo script(
        "x" lineTo script(literal(10)),
        "y" lineTo script(literal(20))
      ),
      typeName lineTo script()
    )
      .compiled(nativeEnvironment)
      .assertEqualTo(
        nativeEnvironment.resolveType(
          compiled(
            "point" lineTo compiled(
              "x" lineTo nativeNumberCompiled(10.0.native),
              "y" lineTo nativeNumberCompiled(20.0.native)))))
  }

  @Test
  fun as_matching() {
    script(
      line(literal(10)),
      asName lineTo script(numberName))
      .compiled(nativeEnvironment)
      .assertEqualTo(nativeNumberCompiled(10.0.native))
  }

  @Test
  fun as_notMatching() {
    assertFails {
      script(
        line(literal(10)),
        asName lineTo script(anyTextScriptLine))
        .compiled(nativeEnvironment)
    }
  }

  @Test
  fun selectIndex() {
    script(
      pickName lineTo script("foo"),
      dropName lineTo script("bar"))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        compiled(
          expression(
            select(
              choice("foo" lineTo type(), "bar" lineTo type()),
              indexed(0, line(field("foo", compiled()))))),
          type(choice("foo" lineTo type(), "bar" lineTo type()))))
  }

  @Test
  fun select_syntaxError() {
    assertFails {
      script(
        pickName lineTo script("foo"),
        "drup" lineTo script("bar"))
        .compiled(nativeEnvironment)
    }
  }

  @Test
  fun select_noSelection() {
    assertFails {
      script(
        dropName lineTo script("foo"))
        .compiled(nativeEnvironment)
    }
  }

  @Test
  fun do_() {
    script(
      "point" lineTo script(
        "x" lineTo script(literal(10)),
        "y" lineTo script(literal(20))),
      doName lineTo script("point", "y", "number"))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        compiled(
          "point" lineTo compiled(
            "x" lineTo nativeNumberCompiled(10.0.native),
            "y" lineTo nativeNumberCompiled(20.0.native)))
          .apply(
            fn(
              type(
                "point" lineTo type(
                  "x" lineTo nativeNumberType,
                  "y" lineTo nativeNumberType)),
              compiled(
                expression<Native>(variable(0)),
                type(
                  "point" lineTo type(
                    "x" lineTo nativeNumberType,
                    "y" lineTo nativeNumberType)))
                  .get(1)
                  .get(0))))
  }
}