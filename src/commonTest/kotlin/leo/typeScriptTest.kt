package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeScriptTest {
  @Test
  fun structure() {
    type(
      "point" lineTo type(
        "x" lineTo type(numberTypeLine),
        "y" lineTo type(numberTypeLine)
      )
    )
      .script
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
  fun choice() {
    type(
      isName lineTo type(
        choice(
          yesName lineTo type(),
          noName lineTo type()
        )
      )
    )
      .script
      .assertEqualTo(
        script(
          isName lineTo script(
            eitherName lineTo script(yesName lineTo script()),
            eitherName lineTo script(noName lineTo script()))))
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
              eitherName lineTo script("empty"),
              eitherName lineTo script("link" lineTo script(
                recurseName lineTo script(),
                anyNumberScriptLine))))))
  }
}