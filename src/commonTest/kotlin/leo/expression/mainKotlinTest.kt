package leo.expression

import leo.base.assertEqualTo
import leo.base.lines
import kotlin.test.Test

class MainKotlinTest {
	@Test
	fun make() {
		"foo".expression
			.structure
			.make("name").expression
			.mainKotlin.string
			.assertEqualTo(
				lines(
					"@JvmInline value class StringName(val text: String)",
					"fun main() = println(StringName(\"foo\"))")
			)
	}
}