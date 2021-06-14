package leo.named.compiler

import leo.base.assertEqualTo
import leo.choice
import leo.doName
import leo.doingName
import leo.giveName
import leo.line
import leo.lineTo
import leo.literal
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.lineTo
import leo.named.expression.variable
import leo.named.typed.doingTypedLine
import leo.named.typed.get
import leo.named.typed.invoke
import leo.named.typed.lineTo
import leo.named.typed.of
import leo.named.typed.typedExpression
import leo.named.typed.typedLine
import leo.numberTypeLine
import leo.ofName
import leo.orName
import leo.script
import leo.takeName
import leo.textTypeLine
import leo.toName
import leo.type
import leo.typeName
import kotlin.test.Test
import kotlin.test.assertFails

class CompileTest {
	@Test
	fun get_postfix() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))),
			"x" lineTo script())
			.typedExpression
			.assertEqualTo(
				typedExpression(
					"point" lineTo typedExpression(
						"x" lineTo typedExpression(typedLine(literal(10))),
						"y" lineTo typedExpression(typedLine(literal(20)))))
					.get("x"))
	}

	@Test
	fun get_prefix() {
		script(
			"x" lineTo script(
				"point" lineTo script(
					"x" lineTo script(literal(10)),
					"y" lineTo script(literal(20)))))
			.typedExpression
			.assertEqualTo(
				typedExpression(
					"point" lineTo typedExpression(
						"x" lineTo typedExpression(typedLine(literal(10))),
						"y" lineTo typedExpression(typedLine(literal(20)))))
					.get("x"))
	}

	@Test
	fun do_withoutBindings() {
		script(
			"foo" lineTo script(),
			doName lineTo script("bar"))
			.typedExpression
			.assertEqualTo(
				function(expression("bar" lineTo expression()))
					.invoke(expression("foo" lineTo expression()))
					.of(type("bar")))
	}

	@Test
	fun do_withBinding() {
		script(
			"x" lineTo script(literal(10)),
			doName lineTo script("x"))
			.typedExpression
			.assertEqualTo(
					function(expression(variable(type("x"))))
						.invoke(expression("x" lineTo expression(expressionLine(literal(10)))))
						.of(type("x" lineTo type(numberTypeLine))))
	}

	@Test
	fun doing() {
		script(
			doingName lineTo script(
				"ping" lineTo script(),
				toName lineTo script("pong")))
			.typedExpression
			.assertEqualTo(
				typedExpression(type("ping") doingTypedLine typedExpression("pong" lineTo typedExpression())))
	}

	@Test
	fun doing_inline() {
		script(
			"foo" lineTo script(),
			doingName lineTo script(
				"ping" lineTo script(),
				toName lineTo script("pong")))
			.typedExpression
			.assertEqualTo(
				typedExpression(
					"foo" lineTo typedExpression(),
					type("ping") doingTypedLine typedExpression("pong" lineTo typedExpression())))
	}

	@Test
	fun doing_missingTo() {
		assertFails {
			script(doingName lineTo script()).typedExpression
		}
	}

	@Test
	fun doingGive() {
		script(
			doingName lineTo script(
				"ping" lineTo script(),
				toName lineTo script("pong")),
			giveName lineTo script("ping"))
			.typedExpression
			.assertEqualTo(
				typedExpression(type("ping") doingTypedLine typedExpression("pong" lineTo typedExpression()))
					.invoke(typedExpression("ping" lineTo typedExpression())))
	}

	@Test
	fun doingGive_notFunction() {
		assertFails {
			script(
				"ping" lineTo script(),
				giveName lineTo script("pong"))
				.typedExpression
		}
	}

	@Test
	fun takeDoing() {
		script(
			"ping" lineTo script(),
			takeName lineTo script(
				doingName lineTo script(
					"ping" lineTo script(),
					toName lineTo script("pong"))))
			.typedExpression
			.assertEqualTo(
				typedExpression(type("ping") doingTypedLine typedExpression("pong" lineTo typedExpression()))
					.invoke(typedExpression("ping" lineTo typedExpression())))
	}

	@Test
	fun valueType() {
		script(
			line(literal("foo")),
			typeName lineTo script())
			.typedExpression
			.assertEqualTo(type(textTypeLine).typedExpression)
	}


	@Test
	fun of() {
		script(
			"yes" lineTo script(),
			ofName lineTo script(
				"yes" lineTo script(),
				orName lineTo script("no")))
			.typedExpression
			.assertEqualTo(
				typedExpression("yes" lineTo typedExpression())
					.of(type(choice("yes" lineTo type(), "no" lineTo type()))))
	}
}