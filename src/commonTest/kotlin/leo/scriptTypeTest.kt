package leo.type.compiler

import leo.base.assertEqualTo
import leo.choice
import leo.choiceName
import leo.lineTo
import leo.orName
import leo.script
import leo.scriptLine
import leo.type
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextTypeLine
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
      nativeNumberTypeLine.scriptLine,
      nativeTextTypeLine.scriptLine)
      .type
      .assertEqualTo(type(nativeNumberTypeLine, nativeTextTypeLine))
  }

  @Test
  fun choice() {
    script(
      choiceName lineTo script(
        nativeTextTypeLine.scriptLine,
        nativeNumberTypeLine.scriptLine,
        "foo" lineTo script()))
      .type
      .assertEqualTo(
        type(choice(nativeTextTypeLine, nativeNumberTypeLine, "foo" lineTo type()))
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