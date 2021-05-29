package leo25.parser

import leo25.base.assertEqualTo
import leo25.stack
import leo25.fieldTo
import leo25.line
import leo25.lineTo
import leo25.literal
import leo25.script
import leo25.ValueError
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ScriptParser {
	@Test
	fun literals() {
		scriptParser.run {
			parsed("123\n").assertEqualTo(script(line(literal(123))))
			parsed("\"123\"\n").assertEqualTo(script(line(literal("123"))))
		}
	}

	@Test
	fun structs() {
		scriptParser.run {
			parsed("").assertEqualTo(script())
			parsed("foo\n").assertEqualTo(script("foo" lineTo script()))
			parsed("foo\nbar\n").assertEqualTo(script("foo" lineTo script(), "bar" lineTo script()))
			parsed("foo\n  bar\n").assertEqualTo(script("foo" lineTo script("bar" lineTo script())))

			parsed("point\n  x\n    10\n  y\n    20\n")
				.assertEqualTo(
					script(
						"point" lineTo script(
							"x" lineTo script(literal(10)),
							"y" lineTo script(literal(20))
						)
					)
				)
		}
	}

	@Test
	fun spacedField() {
		scriptFieldParser.run {
			parsed("foo\n").assertEqualTo("foo" fieldTo script())
			parsed("foo bar\n").assertEqualTo("foo" fieldTo script("bar"))
			parsed("foo bar zoo\n").assertEqualTo("foo" fieldTo script("bar" fieldTo script("zoo")))
			parsed("foo\n  bar\n").assertEqualTo("foo" fieldTo script("bar"))
			parsed("foo\n  bar\n    zoo\n").assertEqualTo("foo" fieldTo script("bar" fieldTo script("zoo")))

			parsed("point\n  x 10\n  y 20\n")
				.assertEqualTo(
					"point" fieldTo script(
						"x" lineTo script(literal(10)),
						"y" lineTo script(literal(20))
					)
				)
		}
	}

	@Test
	fun chains() {
//		scriptParser.run {
//			parsed("foo.bar\n").assertEqualTo(script("foo" lineTo script(), "bar" lineTo script()))
//		}
	}

	@Test
	fun dottedNameStack() {
		dottedNameStackParser.run {
			parsed(".").assertEqualTo(null)
			parsed(".f").assertEqualTo(stack("f"))
			parsed(".foo").assertEqualTo(stack("foo"))
			parsed(".foo.").assertEqualTo(null)
			parsed(".foo.bar").assertEqualTo(stack("foo", "bar"))
			parsed(".foo.bar\n").assertEqualTo(null)
		}
	}

	@Test
	fun scriptBlockParser() {
		scriptBlockParser.run {
			parsed("foo\n").assertEqualTo(script("foo"))
			parsed("foo\n  bar\n").assertEqualTo(script("foo" lineTo script("bar")))
			parsed("foo bar\n").assertEqualTo(script("foo" lineTo script("bar")))
			parsed("foo.bar\n").assertEqualTo(script("foo" lineTo script(), "bar" lineTo script()))
			parsed("foo bar goo\n").assertEqualTo(script("foo" lineTo script("bar" lineTo script("goo"))))
			parsed("foo.bar.goo\n").assertEqualTo(script(line("foo"), line("bar"), line("goo")))
			parsed("foo bar.goo\n").assertEqualTo(script("foo" lineTo script(line("bar"), line("goo"))))

			parsed("\"foo\"\n").assertEqualTo(script(literal("foo")))
			parsed("\"foo\".bar\n").assertEqualTo(script(line(literal("foo")), line("bar")))
			parsed("123.bar\n").assertEqualTo(script(line(literal(123)), line("bar")))
		}
	}

	@Test
	fun syntaxError() {
		assertFailsWith<ValueError> {
			"*".scriptOrThrow
		}
	}
}