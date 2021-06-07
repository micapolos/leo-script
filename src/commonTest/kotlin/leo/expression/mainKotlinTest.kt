package leo.expression

import leo.base.assertEqualTo
import leo.base.lines
import leo.literal
import kotlin.test.Test

class MainKotlinTest {
	@Test
	fun make() {
		"Michał Pociecha-Łoś".literal.expression
			.structure
			.make("name")
			.expression
			.structure
			.make("full")
			.expression
			.mainKotlin.string
			.assertEqualTo(
				lines(
					"@kotlin.jvm.JvmInline value class StringName(val text: String)",
					"@kotlin.jvm.JvmInline value class StringNameFull(val name: StringName)",
					"inline fun name(text: String) = StringName(text)",
					"inline fun full(name: StringName) = StringNameFull(name)",
					"fun main() = println(full(name(\"Michał Pociecha-Łoś\")))"))
	}

	@Test
	fun boolean() {
		false.structure
			.make("check")
			.expression
			.mainKotlin.string
			.assertEqualTo(
				lines(
					"@kotlin.jvm.JvmInline value class BooleanCheck(val is_: Boolean)",
					"inline fun check(is_: Boolean) = BooleanCheck(is_)",
					"fun main() = println(check(false))"))
	}
}