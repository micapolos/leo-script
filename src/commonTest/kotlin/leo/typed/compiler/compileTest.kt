package leo.typed.compiler

import leo.applyName
import leo.asName
import leo.atom
import leo.base.assertEqualTo
import leo.beName
import leo.choice
import leo.doName
import leo.doingName
import leo.dropName
import leo.function
import leo.functionLineTo
import leo.functionName
import leo.getName
import leo.givingName
import leo.haveName
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.makeName
import leo.notName
import leo.numberName
import leo.pickName
import leo.plusName
import leo.quoteName
import leo.repeatName
import leo.script
import leo.selectName
import leo.switchName
import leo.textName
import leo.theName
import leo.type
import leo.typeName
import leo.typed.compiled.apply
import leo.typed.compiled.bind
import leo.typed.compiled.binding
import leo.typed.compiled.body
import leo.typed.compiled.case
import leo.typed.compiled.compiled
import leo.typed.compiled.compiledSelect
import leo.typed.compiled.expression
import leo.typed.compiled.field
import leo.typed.compiled.fn
import leo.typed.compiled.get
import leo.typed.compiled.line
import leo.typed.compiled.lineTo
import leo.typed.compiled.nativeCompiled
import leo.typed.compiled.nativeLine
import leo.typed.compiled.nativeNumberCompiled
import leo.typed.compiled.nativeNumberCompiledLine
import leo.typed.compiled.not
import leo.typed.compiled.recursive
import leo.typed.compiled.rhs
import leo.typed.compiled.select
import leo.typed.compiled.switch
import leo.typed.compiled.the
import leo.typed.compiled.variable
import leo.typed.compiler.native.DoublePlusDoubleNative
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.native
import leo.typed.compiler.native.nativeCompiler
import leo.typed.compiler.native.nativeEnvironment
import leo.typed.compiler.native.nativeNumberType
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextTypeLine
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
          "point" lineTo compiled(
            "x" lineTo compiled<Native>("zero"),
            "y" lineTo compiled("one")))
          .get("x"))
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
          .apply(fn(nativeNumberType, recursive(body(compiled(expression(variable(type("number"))), nativeNumberType))))))
  }

  @Test
  fun repeat_recursion() {
    script(
      "loop" lineTo script(literal(0)),
      repeatName lineTo script(
        givingName lineTo script("nothing"),
        doingName lineTo script("loop" lineTo script(literal(0)))))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        compiled("loop" lineTo nativeNumberCompiled(0.0.native))
          .apply(
            fn(
              type("loop" lineTo nativeNumberType),
              recursive(
                body(
                  compiled("loop" lineTo nativeNumberCompiled(0.0.native))
                    .apply(
                      compiled(
                        expression(variable(type("loop" lineTo nativeNumberType))),
                        type(line(atom(
                          function(
                            type("loop" lineTo nativeNumberType),
                            type("nothing"))))))))))))
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
          .do_(body(compiled(expression(variable(type(numberName))), nativeNumberType))))
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
  fun select() {
    script(
      selectName lineTo script(
        theName lineTo script(literal(10)),
        notName lineTo script(textName)))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        compiledSelect<Native>()
          .the(compiled(nativeLine(10.0.native), nativeNumberTypeLine))
          .not(nativeTextTypeLine)
          .compiled)
  }

  @Test
  fun select_empty() {
    assertFails {
      script(selectName lineTo script())
        .compiled(nativeEnvironment)
    }
  }

  @Test
  fun switch_simple() {
    nativeEnvironment
      .compiled(
        script(
          "deep" lineTo script(
            selectName lineTo script(
              theName lineTo script("one"),
              notName lineTo script("two"),
              notName lineTo script("three"))),
          switchName lineTo script(
            "one" lineTo script(doingName lineTo script(literal(1))),
            "two" lineTo script(doingName lineTo script(literal(2))),
            "three" lineTo script(doingName lineTo script(literal(3))))))
      .assertEqualTo(
        compiled(
          "deep" lineTo
              compiledSelect<Native>()
                .the("one" lineTo compiled())
                .not("two" lineTo type())
                .not("three" lineTo type())
                .compiled)
          .rhs
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
          "deep" lineTo script(
            selectName lineTo script(
              theName lineTo script("one" lineTo script(literal(10))),
              notName lineTo script("two" lineTo script(numberName)),
              notName lineTo script("three" lineTo script(numberName)))),
          switchName lineTo script(
            "one" lineTo script(doingName lineTo script("one", "number")),
            "two" lineTo script(doingName lineTo script("two", "number")),
            "three" lineTo script(doingName lineTo script("three", "number"))
          )
        )
      )
      .assertEqualTo(
        compiled(
          "deep" lineTo
            compiledSelect<Native>()
              .the("one" lineTo nativeNumberCompiled(10.0.native))
              .not("two" lineTo nativeNumberType)
              .not("three" lineTo nativeNumberType)
              .compiled)
          .rhs
          .switch(
            nativeNumberType,
            compiled(expression<Native>(variable(type("one"))), type("one" lineTo nativeNumberType)).get(numberName),
            compiled(expression<Native>(variable(type("two"))), type("two" lineTo nativeNumberType)).get(numberName),
            compiled(expression<Native>(variable(type("three"))), type("three" lineTo nativeNumberType)).get(numberName)))
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
        asName lineTo script(textName))
        .compiled(nativeEnvironment)
    }
  }

  @Test
  fun selectIndex() {
    script(
      selectName lineTo script(
        theName lineTo script("foo"),
        notName lineTo script("bar")))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        compiled(
          expression(
            select(
              choice("foo" lineTo type(), "bar" lineTo type()),
              case("foo", line(field("foo", compiled()))))),
          type(choice("foo" lineTo type(), "bar" lineTo type()))))
  }

  @Test
  fun select_notPickDrop() {
    assertFails {
      script(
        selectName lineTo script(
          pickName lineTo script("foo"),
          "drup" lineTo script("bar")))
        .compiled(nativeEnvironment)
    }
  }

  @Test
  fun select_noPick() {
    assertFails {
      script(selectName lineTo script(dropName lineTo script("foo")))
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
                expression<Native>(variable(type("point"))),
                type(
                  "point" lineTo type(
                    "x" lineTo nativeNumberType,
                    "y" lineTo nativeNumberType)))
                  .get("y")
                  .get("number"))))
  }

  @Test
  fun the() {
    script(
      "red" lineTo script(),
      "color" lineTo script(),
      theName lineTo script(
        "blue" lineTo script(),
        "color" lineTo script()))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        compiled(
          "color" lineTo compiled("red"),
          "color" lineTo compiled("blue")))
  }

  @Test
  fun the_multiline() {
    assertFails {
      script(
        "red" lineTo script(),
        "color" lineTo script(),
        theName lineTo script(
          "x" lineTo script("zero"),
          "y" lineTo script("one")))
        .compiled(nativeEnvironment)
    }
  }

  @Test
  fun have() {
    script(
      "my" lineTo script("color"),
      haveName lineTo script("red"))
      .compiled(nativeEnvironment)
      .assertEqualTo(compiled("my" lineTo compiled("color" lineTo compiled("red"))))
  }

  @Test
  fun make() {
    script(
      "red" lineTo script(),
      makeName lineTo script("favourite" lineTo script("color")))
      .compiled(nativeEnvironment)
      .assertEqualTo(compiled("favourite" lineTo compiled("color" lineTo compiled("red"))))
  }

  @Test
  fun doubleLet() {
    script(
      letName lineTo script(
        "chicken" lineTo script(),
        beName lineTo script("egg")),
      letName lineTo script(
        "human" lineTo script(),
        beName lineTo script("chicken")),
      "human" lineTo script())
      .compiled(nativeEnvironment)
      .assertEqualTo(
        compiled(
          expression(
            bind(
              binding(
                type("chicken"),
                compiled("egg")),
              compiled(
                expression(
                  bind(
                    binding(
                      type("human"),
                      compiled(expression(variable(type("chicken"))), type("egg"))),
                    compiled(expression(variable(type("human"))), type("egg")))),
                type("egg")))),
          type("egg")))
  }
}