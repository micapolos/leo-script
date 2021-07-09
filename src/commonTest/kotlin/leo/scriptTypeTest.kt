package leo.type.compiler

import leo.anyNumberScriptLine
import leo.anyTextScriptLine
import leo.base.assertEqualTo
import leo.choice
import leo.choiceName
import leo.lineTo
import leo.numberTypeLine
import leo.orName
import leo.script
import leo.textTypeLine
import leo.type
import kotlin.test.Test
import kotlin.test.assertFails

class ScriptTest {
  @Test
  fun empty() {
    script().type.assertEqualTo(type())
  }

  @Test
  fun name() {
    script("foo").type.assertEqualTo(type("foo" lineTo type()))
  }

  @Test
  fun names() {
    script(
      "foo" lineTo script(),
      "bar" lineTo script()
    )
      .type
      .assertEqualTo(type("foo" lineTo type(), "bar" lineTo type()))
  }

  @Test
  fun field() {
    script("foo" lineTo script("bar"))
      .type
      .assertEqualTo(type("foo" lineTo type("bar" lineTo type())))
  }

  @Test
  fun fields() {
    script(
      "foo" lineTo script("bar"),
      "zoo" lineTo script("zar")
    )
      .type
      .assertEqualTo(
        type(
          "foo" lineTo type("bar" lineTo type()),
          "zoo" lineTo type("zar" lineTo type())
        )
      )
  }

  @Test
  fun literal() {
    script(
      anyTextScriptLine,
      anyNumberScriptLine
    )
      .type
      .assertEqualTo(type(textTypeLine, numberTypeLine))
  }

  @Test
  fun choice() {
    script(
      choiceName lineTo script(
        anyTextScriptLine,
        anyNumberScriptLine,
        "foo" lineTo script()
      )
    )
      .type
      .assertEqualTo(
        type(choice(textTypeLine, numberTypeLine, "foo" lineTo type()))
      )
  }

  @Test
  fun or_rhsIsMultiLine() {
    assertFails {
      script(
        orName lineTo script(
          "x" lineTo script("zero"),
          "y" lineTo script("one")
        )
      ).type.assertEqualTo(null)
    }
  }
}