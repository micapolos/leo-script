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
		script("foo" lineTo script(literal("bar"))).string.assertEqualTo("foo \"bar\"\n")
	}

	@Test
	fun complexField() {
		script("foo" lineTo script("bar" lineTo script(), "zoo" lineTo script()))
			.notation
			.string
			.assertEqualTo("foo bar.zoo\n")
	}

	@Test
	fun dottedNames2() {
		script("foo" lineTo script(), "bar" lineTo script())
			.notation
			.string
			.assertEqualTo("foo.bar\n")
	}

	@Test
	fun dottedNames3() {
		script("foo" lineTo script(), "bar" lineTo script(), "zoo" lineTo script())
			.notation
			.string
			.assertEqualTo("foo.bar.zoo\n")
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
	fun number() {
		script(literal(123))
			.string
			.assertEqualTo("123\n")
	}
}