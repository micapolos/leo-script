package leo.typed.evaluator

import leo.base.assertEqualTo
import leo.beName
import leo.doName
import leo.doingName
import leo.dropName
import leo.eitherName
import leo.equalName
import leo.givingName
import leo.isName
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.natives.minusName
import leo.noName
import leo.numberName
import leo.pickName
import leo.plusName
import leo.repeatName
import leo.script
import leo.switchName
import leo.textName
import leo.toName
import leo.typeName
import leo.typed.compiler.native.nativeNumberType
import leo.typed.compiler.native.nativeTextType
import leo.typesName
import leo.yesName
import kotlin.test.Test

class EvaluateTest {
  @Test
  fun numberType() {
    script(
      line(literal(10)),
      typeName lineTo script()
    )
      .evaluate
      .assertEqualTo(nativeNumberType.script)
  }

  @Test
  fun textType() {
    script(
      line(literal("foo")),
      typeName lineTo script())
      .evaluate
      .assertEqualTo(nativeTextType.script)
  }

  @Test
  fun selectType() {
    script(
      "id" lineTo script(
        pickName lineTo script(literal(10)),
        dropName lineTo script(textName)),
      typeName lineTo script())
      .evaluate
      .assertEqualTo(
        script(
          "id" lineTo script(
            eitherName lineTo nativeNumberType.script,
            eitherName lineTo nativeTextType.script)))
  }

  @Test
  fun switch_simple() {
    script(
      "is" lineTo script(
        pickName lineTo script("yes"),
        dropName lineTo script("no")),
      switchName lineTo script(
        "yes" lineTo script(doingName lineTo script(literal("OK"))),
        "no" lineTo script(doingName lineTo script(literal("not OK")))))
      .evaluate
      .assertEqualTo(script(literal("OK")))
  }

  @Test
  fun switch() {
    script(
      "id" lineTo script(
        pickName lineTo script("one" lineTo script(literal(10))),
        dropName lineTo script("two" lineTo script(numberName))),
      switchName lineTo script(
        "one" lineTo script(doingName lineTo script("one", "number")),
        "two" lineTo script(doingName lineTo script("two", "number"))))
      .evaluate
      .assertEqualTo(script(literal(10)))
  }

  @Test
  fun switch_secondOfTwo() {
    script(
      "id" lineTo script(
        dropName lineTo script("one" lineTo script(numberName)),
        pickName lineTo script("two" lineTo script(literal(20)))),
      switchName lineTo script(
        "one" lineTo script(doingName lineTo script("one", "number")),
        "two" lineTo script(doingName lineTo script("two", "number"))))
      .evaluate
      .assertEqualTo(script(literal(20)))
  }

  @Test
  fun type() {
    script(
      "point" lineTo script(
        "x" lineTo script(literal(10)),
        "y" lineTo script(literal(20))
      ),
      typeName lineTo script())
      .evaluate
      .assertEqualTo(
        script(
          "point" lineTo script(
            "x" lineTo nativeNumberType.script,
            "y" lineTo nativeNumberType.script)))
  }

  @Test
  fun numberPlusNumber() {
    script(
      line(literal(10)),
      plusName lineTo script(literal(20)))
      .evaluate
      .assertEqualTo(script(literal(30)))
  }

  @Test
  fun do_() {
    script(
      "point" lineTo script(
        "x" lineTo script(literal(10)),
        "y" lineTo script(literal(20))),
      doName lineTo script("point", "y", "number"))
      .evaluate
      .assertEqualTo(script(literal(20)))
  }

  @Test
  fun repeat() {
    script(
      line(literal(10)),
      "countdown" lineTo script(),
      repeatName lineTo script(
        givingName lineTo script(textName),
        doingName lineTo script(
          "countdown" lineTo script(),
          "number" lineTo script(),
          isName lineTo script(equalName lineTo script(toName lineTo script(literal(0)))),
          switchName lineTo script(
            yesName lineTo script(doingName lineTo script(literal("OK"))),
            noName lineTo script(
              doingName lineTo script(
                "countdown" lineTo script(),
                "number" lineTo script(),
                minusName lineTo script(literal(1)),
                "countdown" lineTo script()))))))
      .evaluate
      .assertEqualTo(script(literal("OK")))
  }

  @Test
  fun types() {
    script(
      typesName lineTo script(
        letName lineTo script(
          "ping" lineTo script(),
          doName lineTo script("pong"))),
      letName lineTo script(
        "ping" lineTo script(),
        doName lineTo script(literal("OK"))),
      "pong" lineTo script())
      .evaluate
      .assertEqualTo(script(literal("OK")))
  }

  @Test
  fun be() {
    script(
      "ugly" lineTo script("bastard"),
      beName lineTo script("ugly", "more"))
      .evaluate
      .assertEqualTo(script("more" lineTo script("ugly")))
  }

  @Test
  fun letBe() {
    script(
      letName lineTo script(
        "ugly" lineTo script("bastard"),
        beName lineTo script("ugly", "more")),
      "ugly" lineTo script("bastard"))
      .evaluate
      .assertEqualTo(script("more" lineTo script("ugly")))
  }

  @Test
  fun letDo() {
    script(
      letName lineTo script(
        "ugly" lineTo script("bastard"),
        doName lineTo script("ugly", "more")),
      "ugly" lineTo script("bastard"))
      .evaluate
      .assertEqualTo(script("more" lineTo script("ugly" lineTo script("bastard"))))
  }

  @Test
  fun letRepeat() {
    script(
      letName lineTo script(
        numberName lineTo script(),
        "countdown" lineTo script(),
        repeatName lineTo script(
          givingName lineTo script(textName),
          doingName lineTo script(
            "countdown" lineTo script(),
            "number" lineTo script(),
            isName lineTo script(equalName lineTo script(toName lineTo script(literal(0)))),
            switchName lineTo script(
              yesName lineTo script(doingName lineTo script(literal("OK"))),
              noName lineTo script(
                doingName lineTo script(
                  "countdown" lineTo script(),
                  "number" lineTo script(),
                  minusName lineTo script(literal(1)),
                  "countdown" lineTo script())))))),
      line(literal(10)),
      "countdown" lineTo script())
      .evaluate
      .assertEqualTo(script(literal("OK")))
  }
}