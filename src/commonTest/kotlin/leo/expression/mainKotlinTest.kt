package leo.expression

import leo.base.assertEqualTo
import leo.base.lines
import kotlin.test.Test

class MainKotlinTest {
	@Test
	fun make() {
		"Michał Pociecha-Łoś".expression
			.structure
			.make("name").expression
			.structure.make("full").expression
			.mainKotlin.string
			.assertEqualTo(
				lines(
					"@kotlin.jvm.JvmInline value class StringName(val text: String)",
					"@kotlin.jvm.JvmInline value class StringNameFull(val name: StringName)",
					"fun name(text: String) = StringName(text)",
					"fun full(name: StringName) = StringNameFull(name)",
					"fun main() = println(full(name(\"Michał Pociecha-Łoś\")))"))
	}
}