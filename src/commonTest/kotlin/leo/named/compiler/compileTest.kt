package leo.named.compiler

import leo.base.assertEqualTo
import leo.beName
import leo.bindName
import leo.choice
import leo.choiceName
import leo.doName
import leo.doingLineTo
import leo.doingName
import leo.giveName
import leo.givenName
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.named.expression.be
import leo.named.expression.bind
import leo.named.expression.body
import leo.named.expression.caseTo
import leo.named.expression.do_
import leo.named.expression.doingLineTo
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.give
import leo.named.expression.invoke
import leo.named.expression.let
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.make
import leo.named.expression.numberExpression
import leo.named.expression.rhs
import leo.named.expression.switch
import leo.named.expression.take
import leo.named.typed.get
import leo.named.typed.lineTo
import leo.named.typed.of
import leo.named.typed.typed
import leo.named.typed.typedExpression
import leo.named.typed.typedLine
import leo.numberTypeLine
import leo.ofName
import leo.script
import leo.switchName
import leo.takeName
import leo.textTypeLine
import leo.toName
import leo.type
import leo.typeName
import kotlin.test.Test
import kotlin.test.assertFails

class CompileTest {
	@Test
	fun be() {
		script(
			line(literal(10)),
			beName lineTo script(literal(20)))
			.typedExpression
			.assertEqualTo(
				10.numberExpression
					.be(20.numberExpression)
					.of(type(numberTypeLine)))
	}

	@Test
	fun bind() {
		script(
			bindName lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))),
			"x" lineTo script())
			.typedExpression
			.assertEqualTo(
				expression(
					line(
						bind(expression(
						"x" lineTo 10.numberExpression,
						"y" lineTo 20.numberExpression))),
					line(make("x")),
					line(invoke(type("x"))))
					.of(type("x" lineTo type(numberTypeLine))))
	}

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
				typed(
					expression(
						line(make("foo")),
						line(do_(body(expression(line(make("bar"))))))),
					type("bar")))
	}

	@Test
	fun do_withBinding() {
		script(
			"x" lineTo script(literal(10)),
			doName lineTo script("x"))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						"x" lineTo expression(expressionLine(literal(10))),
						line(do_(body(expression(
							line(make("x")),
							line(invoke(type("x")))))))),
					type("x" lineTo type(numberTypeLine))))
	}

	@Test
	fun doing() {
		script(
			"foo" lineTo script(),
			doingName lineTo script(
				"ping" lineTo script(),
				toName lineTo script("pong")))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						line(make("foo")),
						type("ping") doingLineTo body(expression(line(make("pong"))))),
					type(
						"foo" lineTo type(),
						type("ping") doingLineTo type("pong"))))
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
				typed(
					expression(
						type("ping") doingLineTo body(expression(line(make("pong")))),
						line(give(expression(line(make("ping")))))),
					type("pong")))
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
	fun letBe() {
		script(
			"foo" lineTo script(),
			letName lineTo script(
				"ping" lineTo script(),
				beName lineTo script(givenName)))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						line(make("foo")),
						line(let(type("ping"), rhs(be(expression(line(make(givenName)))))))),
					type("foo")))
	}

	@Test
	fun letDo() {
		script(
			"foo" lineTo script(),
			letName lineTo script(
				"ping" lineTo script(),
				doName lineTo script(givenName)))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						line(make("foo")),
						line(let(type("ping"), rhs(do_(body(expression(line(make(givenName)), line(invoke(type(givenName)))))))))),
					type("foo")))
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
				typed(
					expression(
						line(make("ping")),
						line(take(expression(
							type("ping") doingLineTo body(expression(line(make("pong")))))))),
					type("pong")))
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
				choiceName lineTo script(
					"yes" lineTo script(),
					"no" lineTo script())))
			.typedExpression
			.assertEqualTo(
				expression().make("yes").of(type(choice("yes" lineTo type(), "no" lineTo type()))))
	}

	@Test
	fun switch() {
		script(
			"boolean" lineTo script(
				"yes" lineTo script(),
					ofName lineTo script(
						choiceName lineTo script(
							"yes" lineTo script(),
							"no" lineTo script()))),
			switchName lineTo script(
				"yes" lineTo script(literal(10)),
				"no" lineTo script(literal(20))))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						"boolean" lineTo expression(line(make("yes"))),
						line(
							switch(
							"yes" caseTo 10.numberExpression,
							"no" caseTo 20.numberExpression)
						)),
					type(numberTypeLine)))
	}
}