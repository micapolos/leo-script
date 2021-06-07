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
import leo.literal
import leo.numberTypeLine
import leo.textTypeLine
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
			.bind("x".variable.op.of("x" lineTo type(numberTypeLine)))
			.expression
			.kotlin.string
			.assertEqualTo("x(10).let { x -> y(20).let { y -> x } }")
	}

	@Test
	fun invoke() {
		structure(
			"x" fieldTo 10.number,
			"y" fieldTo 20.number)
			.invoke.op.of(textTypeLine)
			.kotlin.string
			.assertEqualTo("_invoke(x(10), y(20))")
	}

	@Test
	fun isConstant() {
		structure(false.expression)
			.kotlin.string
			.assertEqualTo("false")

		structure(true.expression)
			.kotlin.string
			.assertEqualTo("true")
	}

	@Test
	fun equal() {
		10.literal.expression
			.resolveEqual(20.literal.expression)
			.kotlin.string
			.assertEqualTo("10.equals(20)")
	}
}