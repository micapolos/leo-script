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
					"@kotlin.jvm.JvmInline value class StringName(val text: String)",
					"fun name(val text: String) = StringName(text)",
					"fun main() = println(name(\"foo\"))")
			)
	}
}