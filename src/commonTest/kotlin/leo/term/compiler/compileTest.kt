package leo.term.compiler

import leo.anyNumberScriptLine
import leo.anyTextScriptLine
import leo.applyName
import leo.asName
import leo.base.assertEqualTo
import leo.doName
import leo.doingName
import leo.dropName
import leo.equalsName
import leo.functionName
import leo.getName
import leo.giveName
import leo.line
import leo.lineTo
import leo.literal
import leo.natives.minusName
import leo.noName
import leo.numberName
import leo.numberType
import leo.numberTypeLine
import leo.pickName
import leo.plus
import leo.quoteName
import leo.repeatingName
import leo.script
import leo.switchName
import leo.term.compiled.apply
import leo.term.compiled.body
import leo.term.compiled.compiled
import leo.term.compiled.do_
import leo.term.compiled.drop
import leo.term.compiled.expression
import leo.term.compiled.fn
import leo.term.compiled.lineTo
import leo.term.compiled.nativeLine
import leo.term.compiled.nativeNumberCompiled
import leo.term.compiled.nativeNumberCompiledLine
import leo.term.compiled.pick
import leo.term.compiler.native.Native
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeCompiler
import leo.term.compiler.native.nativeEnvironment
import leo.term.compiler.native.objectEqualsObject
import leo.term.eitherFirst
import leo.term.eitherSecond
import leo.term.fn
import leo.term.get
import leo.term.head
import leo.term.id
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.tail
import leo.term.typed.lineTo
import leo.term.typed.typed
import leo.term.typed.typedTerm
import leo.term.variable
import leo.textTypeLine
import leo.type
import leo.typeName
import leo.yesName
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
      .assertEqualTo(compiled(compiled(nativeLine(10.0.native), numberTypeLine)))
  }

  @Test
  fun text() {
    nativeEnvironment
      .compiled(script(literal("foo")))
      .assertEqualTo(compiled(compiled(nativeLine("foo".native), textTypeLine)))
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
          .apply(fn(numberType, compiled("ok")))
      )
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
          .do_(body(compiled(expression(variable(0)), numberType))))
  }

  @Test
  fun numberEqualsNumber() {
    nativeEnvironment
      .compiled(
        script(
          line(literal(10)),
          equalsName lineTo script(literal(20))
        )
      )
      .assertEqualTo(
        typedTerm(
          typed(10.0.native.nativeTerm, numberTypeLine),
          equalsName lineTo typedTerm(typed(20.0.native.nativeTerm, numberTypeLine))
        ).let {
          typed(fn(get<Native>(0).tail.objectEqualsObject(get<Native>(0).head)).invoke(it.v), type(equalsTypeLine))
        })
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
          dropName lineTo script(anyTextScriptLine)
        )
      )
      .assertEqualTo(
        compiled<Native>()
          .pick(compiled(compiled(nativeLine(10.0.native), numberTypeLine)))
          .drop(type(textTypeLine))
      )
  }

  @Test
  fun switch_firstOfTwo() {
    nativeEnvironment
      .compiled(
        script(
          "id" lineTo script(
            pickName lineTo script("one" lineTo script(literal(10))),
            dropName lineTo script("two" lineTo script(anyNumberScriptLine))
          ),
          switchName lineTo script(
            "one" lineTo script(doingName lineTo script("one", "number")),
            "two" lineTo script(doingName lineTo script("two", "number"))
          )
        )
      )
      .assertEqualTo(
        typed(10.0.native.nativeTerm.eitherFirst.invoke(id()).invoke(id()), type(numberTypeLine))
      )
  }

  @Test
  fun switch_secondOfTwo() {
    nativeEnvironment
      .compiled(
        script(
          "id" lineTo script(
            dropName lineTo script("one" lineTo script(anyNumberScriptLine)),
            pickName lineTo script("two" lineTo script(literal(20)))
          ),
          switchName lineTo script(
            "one" lineTo script(doingName lineTo script("one", "number")),
            "two" lineTo script(doingName lineTo script("two", "number"))
          )
        )
      )
      .assertEqualTo(
        typed(20.0.native.nativeTerm.eitherSecond.invoke(id()).invoke(id()), type(numberTypeLine))
      )
  }

  @Test
  fun switch_firstOfThree() {
    nativeEnvironment
      .compiled(
        script(
          "id" lineTo script(
            pickName lineTo script("one" lineTo script(literal(10))),
            dropName lineTo script("two" lineTo script(anyNumberScriptLine)),
            dropName lineTo script("three" lineTo script(anyNumberScriptLine))),
          switchName lineTo script(
            "one" lineTo script(doingName lineTo script("one", "number")),
            "two" lineTo script(doingName lineTo script("two", "number")),
            "three" lineTo script(doingName lineTo script("three", "number"))
          )
        )
      )
      .assertEqualTo(
        typed(10.0.native.nativeTerm.eitherFirst.eitherFirst.invoke(fn(get<Native>(0).invoke(id()).invoke(id()))).invoke(id()), type(numberTypeLine))
      )
  }

  @Test
  fun type() {
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
              "x" lineTo compiled(compiled(nativeLine(10.0.native), numberTypeLine)),
              "y" lineTo compiled(compiled(nativeLine(20.0.native), numberTypeLine))
            )
          )
        )
      )
  }

  @Test
  fun giveDoing() {
    script(
      line(literal(1)),
      giveName lineTo script(
        "ok" lineTo script(anyNumberScriptLine),
        doingName lineTo script("ok" lineTo script(numberName))))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        script(line(literal(1)))
          .compiled(nativeEnvironment)
          .do_(
            body(
              nativeEnvironment.context
                .bind(type(numberTypeLine))
                .compiled(script("ok" lineTo script(numberName))))))
  }

  @Test
  fun giveRepeating() {
    val inputScript = script(
      line(literal(10)),
      "countdown" lineTo script())

    val doingScript =
      script(
        numberName lineTo script(),
        equalsName lineTo script(literal(0)),
        switchName lineTo script(
          yesName lineTo script(doingName lineTo script(literal("OK"))),
          noName lineTo script(
            doingName lineTo script(
              numberName lineTo script(),
              minusName lineTo script(literal(1)),
              "countdown" lineTo script()))))

    inputScript
      .plus(
        giveName lineTo script(
          anyTextScriptLine,
          repeatingName lineTo doingScript))
      .compiled(nativeEnvironment)
      .assertEqualTo(
        inputScript
          .compiled(nativeEnvironment)
          .let { typedTerm -> TODO()
//            typedTerm
//              .repeat(
//                nativeEnvironment
//                  .context
//                  .plus(binding(definition(typedTerm.t functionTo type(textTypeLine))))
//                  .plus(binding(given(typedTerm.t)))
//                  .compiled(doingScript))
          })
  }

  @Test
  fun as_matching() {
    script(
      line(literal(10)),
      asName lineTo script(anyNumberScriptLine))
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
}