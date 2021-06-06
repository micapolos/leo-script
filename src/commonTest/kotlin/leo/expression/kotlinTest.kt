package leo.expression

import leo.base.assertEqualTo
import leo.base.lines
import leo.numberTypeLine
import kotlin.test.Test

class KotlinTest {
	@Test
	fun get() {
		"foo".expression
			.get("length").op.of(numberTypeLine)
			.fullKotlin.string
			.assertEqualTo("\"foo\".length")
	}

	@Test
	fun make() {
		"foo".expression
			.structure
			.make("name").expression
			.fullKotlin.string
			.assertEqualTo(
				lines(
					"data class Name(val text: String)",
					"Name(\"foo\")"))
	}
}