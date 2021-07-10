package leo.term.compiler

import leo.anyNumberScriptLine
import leo.anyTextScriptLine
import leo.applyName
import leo.base.assertEqualTo
import leo.doingName
import leo.dropName
import leo.equalsName
import leo.functionName
import leo.functionTo
import leo.getName
import leo.giveName
import leo.line
import leo.lineTo
import leo.literal
import leo.natives.minusName
import leo.noName
import leo.notName
import leo.numberName
import leo.numberTypeLine
import leo.pickName
import leo.plus
import leo.quoteName
import leo.repeatingName
import leo.script
import leo.selectName
import leo.switchName
import leo.term.compiler.native.Native
import leo.term.compiler.native.native
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
import leo.term.typed.choicePlus
import leo.term.typed.do_
import leo.term.typed.invoke
import leo.term.typed.lineTo
import leo.term.typed.noSelection
import leo.term.typed.repeat
import leo.term.typed.typed
import leo.term.typed.typedChoice
import leo.term.typed.typedFunctionLine
import leo.term.typed.typedTerm
import leo.term.typed.yesSelection
import leo.textTypeLine
import leo.theName
import leo.type
import leo.typeName
import leo.yesName
import kotlin.test.Test

class CompileTest {
  @Test
  fun empty() {
    nativeEnvironment
      .typedTerm(script())
      .assertEqualTo(typedTerm())
  }

  @Test
  fun name() {
    nativeEnvironment
      .typedTerm(script("foo"))
      .assertEqualTo(typedTerm("foo" lineTo typedTerm()))
  }

  @Test
  fun field() {
    nativeEnvironment
      .typedTerm(script("foo" lineTo script("bar")))
      .assertEqualTo(typedTerm("foo" lineTo typedTerm("bar" lineTo typedTerm())))
  }

  @Test
  fun number() {
    nativeEnvironment
      .typedTerm(script(literal(10)))
      .assertEqualTo(typedTerm(typed(10.0.native.nativeTerm, numberTypeLine)))
  }

  @Test
  fun text() {
    nativeEnvironment
      .typedTerm(script(literal("foo")))
      .assertEqualTo(typedTerm(typed("foo".native.nativeTerm, textTypeLine)))
  }

  @Test
  fun names() {
    nativeEnvironment
      .typedTerm(script("foo", "bar"))
      .assertEqualTo(typedTerm("bar" lineTo typedTerm("foo")))
  }

  @Test
  fun fields() {
    nativeEnvironment
      .typedTerm(
        script(
          "x" lineTo script("zero"),
          "y" lineTo script("one")
        )
      )
      .assertEqualTo(
        typedTerm(
          "x" lineTo typedTerm("zero" lineTo typedTerm()),
          "y" lineTo typedTerm("one" lineTo typedTerm())
        )
      )
  }

  @Test
  fun get() {
    nativeEnvironment
      .typedTerm(
        script(
          "point" lineTo script(
            "x" lineTo script("zero"),
            "y" lineTo script("one")
          ),
          "x" lineTo script()
        )
      )
      .assertEqualTo(
        typedTerm(
          "x" lineTo typedTerm("zero" lineTo typedTerm())
        )
      )
  }

  @Test
  fun numberEqualsNumber() {
    nativeEnvironment
      .typedTerm(
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
      .typedTerm(
        script(
          functionName lineTo script(
            "zero" lineTo script(),
            doingName lineTo script("one")
          )
        )
      )
      .assertEqualTo(
        typedTerm(
          typedFunctionLine(type("zero"), typedTerm("one" lineTo typedTerm()))
        )
      )
  }

  @Test
  fun apply() {
    nativeEnvironment
      .typedTerm(
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
      .assertEqualTo(
        typedTerm<Native>(typedFunctionLine(type("ping"), typedTerm("pong" lineTo typedTerm())))
          .invoke(typedTerm("ping" lineTo typedTerm()))
      )
  }

  @Test
  fun quote() {
    nativeEnvironment
      .typedTerm(
        script(quoteName lineTo script(getName lineTo script("foo")))
      )
      .assertEqualTo(nativeEnvironment.staticTypedTerm(script(getName lineTo script("foo"))))
  }

  @Test
  fun select() {
    nativeEnvironment
      .typedTerm(
        script(
          selectName lineTo script(
            theName lineTo script(literal(10)),
            notName lineTo script(anyTextScriptLine)
          )
        )
      )
      .assertEqualTo(
        typedChoice<Native>()
          .choicePlus(yesSelection(typed(10.0.native.nativeTerm, numberTypeLine)))
          .choicePlus(noSelection(textTypeLine))
          .typedTerm
      )
  }

  @Test
  fun pickDrop() {
    nativeEnvironment
      .typedTerm(
        script(
          pickName lineTo script(literal(10)),
          dropName lineTo script(anyTextScriptLine)
        )
      )
      .assertEqualTo(
        typedChoice<Native>()
          .choicePlus(yesSelection(typed(10.0.native.nativeTerm, numberTypeLine)))
          .choicePlus(noSelection(textTypeLine))
          .typedTerm
      )
  }

  @Test
  fun switch_firstOfTwo() {
    nativeEnvironment
      .typedTerm(
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
      .typedTerm(
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
      .typedTerm(
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
      .typedTerm(nativeEnvironment)
      .assertEqualTo(
        nativeEnvironment.resolveType(
          typedTerm(
            "point" lineTo typedTerm(
              "x" lineTo typedTerm(typed(10.0.native.nativeTerm, numberTypeLine)),
              "y" lineTo typedTerm(typed(20.0.native.nativeTerm, numberTypeLine))
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
      .typedTerm(nativeEnvironment)
      .assertEqualTo(
        script(line(literal(1)))
          .typedTerm(nativeEnvironment)
          .do_(nativeEnvironment.context
            .plus(binding(given(type(numberTypeLine))))
            .typedTerm(script("ok" lineTo script(numberName)))))
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
      .typedTerm(nativeEnvironment)
      .assertEqualTo(
        inputScript
          .typedTerm(nativeEnvironment)
          .let { typedTerm ->
            typedTerm
              .repeat(
                nativeEnvironment
                  .context
                  .plus(binding(definition(typedTerm.t functionTo type(textTypeLine))))
                  .plus(binding(given(typedTerm.t)))
                  .typedTerm(doingScript))
          })
  }
}