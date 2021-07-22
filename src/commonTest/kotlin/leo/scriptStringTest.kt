package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ScriptStringTest {
  @Test
  fun empty() {
    script().string.assertEqualTo("")
  }

  @Test
  fun name() {
    script("foo").string.assertEqualTo("foo\n")
  }

  @Test
  fun simpleField() {
    script("foo" lineTo script("bar")).string.assertEqualTo("foo bar\n")
  }

  @Test
  fun literalField() {
    script("foo" lineTo script(literal(" bar "))).string.assertEqualTo("foo \" bar \"\n")
  }

  @Test
  fun complexField() {
    script("foo" lineTo script("bar" lineTo script(), "zoo" lineTo script()))
      .notation
      .string
      .assertEqualTo(if (useDottedNotation) "foo bar.zoo\n" else "foo\n  bar\n  zoo\n")
  }

  @Test
  fun dottedNames2() {
    script("foo" lineTo script(), "bar" lineTo script())
      .notation
      .string
      .assertEqualTo(if (useDottedNotation) "foo.bar\n" else "foo\nbar\n")
  }

  @Test
  fun dottedNames3() {
    script("foo" lineTo script(), "bar" lineTo script(), "zoo" lineTo script())
      .notation
      .string
      .assertEqualTo(if (useDottedNotation) "foo.bar.zoo\n" else "foo\nbar\nzoo\n")
  }

  @Test
  fun twoFields() {
    script(
      "foo" lineTo script("bar"),
      "zoo" lineTo script("zar")
    )
      .notation
      .string
      .assertEqualTo("foo bar\nzoo zar\n")
  }

  @Test
  fun twoComplexFields() {
    script(
      "foo" lineTo script(
        "bar" lineTo script("gar"),
        "far" lineTo script("rar")
      ),
      "zoo" lineTo script("zar")
    )
      .notation
      .string
      .assertEqualTo("foo\n  bar gar\n  far rar\nzoo zar\n")
  }

  @Test
  fun text() {
    script(literal("foo"))
      .string
      .assertEqualTo("\"foo\"\n")
  }

  @Test
  fun textMultiline() {
    script(literal("foo\nbar"))
      .string
      .assertEqualTo("text\n  foo\n  bar\n")
  }

  @Test
  fun textDoubleQuotes() {
    script(literal("\"foo\""))
      .string
      .assertEqualTo("text \"foo\"\n")
  }

  @Test
  fun number() {
    script(literal(123))
      .string
      .assertEqualTo("123\n")
  }

  @Test
  fun numberInfinity() {
    script(literal(Double.POSITIVE_INFINITY))
      .string
      .assertEqualTo("number infinity\n")
  }

  @Test
  fun numberMinusInfinity() {
    script(literal(Double.NEGATIVE_INFINITY))
      .string
      .assertEqualTo("number minus infinity\n")
  }

  @Test
  fun numberNone() {
    script(literal(Double.NaN))
      .string
      .assertEqualTo("number none\n")
  }
}