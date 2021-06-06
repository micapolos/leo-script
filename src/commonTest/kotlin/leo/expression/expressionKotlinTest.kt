package leo.expression

import leo.base.assertEqualTo
import leo.expression.dsl.fieldTo
import leo.expression.dsl.get
import leo.expression.dsl.kotlin
import leo.expression.dsl.make
import leo.expression.dsl.number
import leo.expression.dsl.structure
import leo.expression.dsl.text
import leo.lineTo
import leo.numberTypeLine
import leo.type
import kotlin.test.Test

class ExpressionKotlinTest {
	@Test
	fun get() {
		structure(
			"name" fieldTo "foo".text)
			.get("text")
			.kotlin.string
			.assertEqualTo("name(\"foo\").text")
	}

	@Test
	fun make() {
		"foo".text
			.make("name")
			.kotlin.string
			.assertEqualTo("name(\"foo\")")
	}

	@Test
	fun bind() {
		structure(
			"x" fieldTo 10.number,
			"y" fieldTo 20.number)
			.bind("x".binding.op.of("x" lineTo type(numberTypeLine)))
			.expression
			.kotlin.string
			.assertEqualTo("x(10).let { x -> y(20).let { y -> x } }")
	}
}