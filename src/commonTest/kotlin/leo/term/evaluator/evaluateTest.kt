package leo.term.evaluator

import leo.anyNumberScriptLine
import leo.anyTextScriptLine
import leo.base.assertEqualTo
import leo.doName
import leo.doingName
import leo.dropName
import leo.eitherName
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.pickName
import leo.plusName
import leo.script
import leo.switchName
import leo.typeName
import kotlin.test.Test

class EvaluateTest {
  @Test
  fun numberType() {
    script(
      line(literal(10)),
      typeName lineTo script()
    )
      .evaluate
      .assertEqualTo(script(anyNumberScriptLine))
  }

  @Test
  fun textType() {
    script(
      line(literal("foo")),
      typeName lineTo script()
    )
      .evaluate
      .assertEqualTo(script(anyTextScriptLine))
  }

  @Test
  fun selectType() {
    script(
      "id" lineTo script(
        pickName lineTo script(literal(10)),
        dropName lineTo script(anyTextScriptLine)),
      typeName lineTo script())
      .evaluate
      .assertEqualTo(
        script(
          "id" lineTo script(
            eitherName lineTo script(anyNumberScriptLine),
            eitherName lineTo script(anyTextScriptLine))))
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
        dropName lineTo script("two" lineTo script(anyNumberScriptLine))),
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
        dropName lineTo script("one" lineTo script(anyNumberScriptLine)),
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
      typeName lineTo script()
    )
      .evaluate
      .assertEqualTo(
        script(
          "point" lineTo script(
            "x" lineTo script(anyNumberScriptLine),
            "y" lineTo script(anyNumberScriptLine)
          )
        )
      )
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
  fun letType() {
    script(
      letName lineTo script(
        typeName lineTo script(
          "ping" lineTo script(),
          doName lineTo script("pong"))),
      letName lineTo script(
        "ping" lineTo script(),
        doName lineTo script(literal("OK"))),
      "pong" lineTo script())
      .evaluate
      .assertEqualTo(script(literal("OK")))
  }
}