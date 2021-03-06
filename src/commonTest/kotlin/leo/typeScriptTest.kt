package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeScriptTest {
  @Test
  fun structure() {
    type(
      "point" lineTo type(
        "x" lineTo numberType,
        "y" lineTo numberType
      )
    )
      .script
      .assertEqualTo(
        script(
          "point" lineTo script(
            "x" lineTo numberType.script,
            "y" lineTo numberType.script
          )
        )
      )
  }

  @Test
  fun choice() {
    type(
      isName lineTo type(
        choice(
          yesName lineTo type(),
          noName lineTo type())))
      .script
      .assertEqualTo(
        script(
          isName lineTo script(
            choiceName lineTo script(
              yesName lineTo script(),
              noName lineTo script()))))
  }

  @Test
  fun recursive() {
    type(
      line(
        recursive(
          "stack" lineTo type(
            choice(
              "empty" lineTo type(),
              "link" lineTo type(
                recurseTypeLine,
                numberTypeLine
              )
            )
          )
        )
      )
    )
      .script
      .assertEqualTo(
        script(
          recursiveName lineTo script(
            "stack" lineTo script(
              choiceName lineTo script(
                "empty" lineTo script(),
                "link" lineTo script(
                  recurseName lineTo script(),
                  numberTypeLine.scriptLine))))))
  }
}