package leo.term.evaluator

import leo.base.assertEqualTo
import leo.choiceName
import leo.line
import leo.lineTo
import leo.literal
import leo.notName
import leo.numberTypeScriptLine
import leo.script
import leo.selectName
import leo.switchName
import leo.textTypeScriptLine
import leo.theName
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
      .assertEqualTo(script(numberTypeScriptLine))
  }

  @Test
  fun textType() {
    script(
      line(literal("foo")),
      typeName lineTo script()
    )
      .evaluate
      .assertEqualTo(script(textTypeScriptLine))
  }

  @Test
  fun selectType() {
    script(
      selectName lineTo script(
        theName lineTo script(literal(10)),
        notName lineTo script(textTypeScriptLine)
      ),
      typeName lineTo script()
    )
      .evaluate
      .assertEqualTo(
        script(
          choiceName lineTo script(
            numberTypeScriptLine,
            textTypeScriptLine
          )
        )
      )
  }

  @Test
  fun switch() {
    script(
      "id" lineTo script(
        selectName lineTo script(
          theName lineTo script("one" lineTo script(literal(10))),
          notName lineTo script("two" lineTo script(numberTypeScriptLine))
        )
      ),
      switchName lineTo script(
        "one" lineTo script("one", "number"),
        "two" lineTo script("two", "number")
      )
    )
      .evaluate
      .assertEqualTo(script(literal(10)))
  }

  @Test
  fun switch_secondOfTwo() {
    script(
      "id" lineTo script(
        selectName lineTo script(
          notName lineTo script("one" lineTo script(numberTypeScriptLine)),
          theName lineTo script("two" lineTo script(literal(20)))
        )
      ),
      switchName lineTo script(
        "one" lineTo script("one", "number"),
        "two" lineTo script("two", "number")
      )
    )
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
            "x" lineTo script(numberTypeScriptLine),
            "y" lineTo script(numberTypeScriptLine)
          )
        )
      )
  }
}