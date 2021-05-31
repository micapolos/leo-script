package leo.parser

import leo.ValueError
import leo.base.assertEqualTo
import leo.base.fail
import leo.field
import leo.fieldTo
import leo.literal
import leo.script
import leo.value
import kotlin.test.Test

class LocatingParserTest {
	@Test
	fun success() {
		scriptParser.parseOrThrow("foo\n").assertEqualTo(script("foo"))
	}

	@Test
	fun throwing() {
		try {
			scriptParser.parseOrThrow("foo\nbar goo\nzoo\n  123da").assertEqualTo("abd")
			fail<Unit>()
		} catch (e: ValueError) {
			e.value.assertEqualTo(
				value(
					"location" fieldTo value(
						"line" fieldTo value(field(literal(4))),
						"column" fieldTo value(field(literal(6)))
					)
				)
			)
		}
	}
}