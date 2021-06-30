package leo.named.compiler

import leo.base.assertEqualTo
import leo.beName
import leo.bindName
import leo.choice
import leo.choiceName
import leo.defineName
import leo.doName
import leo.doingName
import leo.equalName
import leo.functionLineTo
import leo.functionName
import leo.giveName
import leo.givenName
import leo.givingName
import leo.isName
import leo.isType
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.named.expression.be
import leo.named.expression.bind
import leo.named.expression.caseTo
import leo.named.expression.do_
import leo.named.expression.doing
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.give
import leo.named.expression.invoke
import leo.named.expression.isEqualTo
import leo.named.expression.let
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.make
import leo.named.expression.negate
import leo.named.expression.numberExpression
import leo.named.expression.rhs
import leo.named.expression.select
import leo.named.expression.take
import leo.named.typed.get
import leo.named.typed.lineTo
import leo.named.typed.of
import leo.named.typed.typed
import leo.named.typed.typedExpression
import leo.named.typed.typedLine
import leo.notName
import leo.numberTypeLine
import leo.ofName
import leo.script
import leo.selectName
import leo.takeName
import leo.takingName
import leo.textTypeLine
import leo.theName
import leo.toName
import leo.type
import leo.typeName
import leo.withName
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
						line(do_(doing(expression(line(make("bar"))))))),
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
						line(do_(
							doing(expression(
								line(make("x")),
								line(invoke(type("x")))))
						))),
					type("x" lineTo type(numberTypeLine))))
	}

	@Test
	fun functionTakingDoing() {
		script(
			"foo" lineTo script(),
			functionName lineTo script(
				takingName lineTo script("ping"),
				doingName lineTo script("pong")))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						line(make("foo")),
						type("ping") lineTo doing(expression(line(make("pong"))))
					),
					type(
						"foo" lineTo type(),
						type("ping") functionLineTo type("pong"))))
	}

	@Test
	fun functionTakingGivingDoing() {
		script(
			"foo" lineTo script(),
			functionName lineTo script(
				takingName lineTo script("ping"),
				givingName lineTo script("pong"),
				doingName lineTo script("pong")))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						line(make("foo")),
						type("ping") lineTo doing(expression(line(make("pong"))))
					),
					type(
						"foo" lineTo type(),
						type("ping") functionLineTo type("pong"))))
	}

	@Test
	fun functionTakingGivingDoing_typeError() {
		assertFails {
			script(
				"foo" lineTo script(),
				functionName lineTo script(
					takingName lineTo script("ping"),
					givingName lineTo script("pong"),
					doingName lineTo script("pang")))
				.typedExpression
		}
	}

	@Test
	fun function_empty() {
		script(functionName)
			.typedExpression
			.assertEqualTo(
				typed(
					expression(line(make(functionName))),
					type(functionName)))
	}

	@Test
	fun functionGive() {
		script(
			functionName lineTo script(
				takingName lineTo script("ping"),
				doingName lineTo script("pong")),
			giveName lineTo script("ping"))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						type("ping") lineTo doing(expression(line(make("pong")))),
						line(give(expression(line(make("ping")))))),
					type("pong")))
	}

	@Test
	fun functionGive_notFunction() {
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
						line(let(type("ping"), rhs(do_(doing(expression(line(make(givenName)), line(invoke(type(givenName)))))))))),
					type("foo")))
	}

	@Test
	fun takeFunction() {
		script(
			"ping" lineTo script(),
			takeName lineTo script(
				functionName lineTo script(
					takingName lineTo script("ping"),
					doingName lineTo script("pong"))))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						line(make("ping")),
						line(take(expression(
							type("ping") lineTo doing(expression(line(make("pong"))))
						)))),
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
					theName lineTo script(
						"yes" lineTo script(),
						"no" lineTo script()))))
			.typedExpression
			.assertEqualTo(
				expression().make("yes").of(type(choice("yes" lineTo type(), "no" lineTo type()))))
	}

	@Test
	fun select() {
		script(
			"boolean" lineTo script(
				"yes" lineTo script(),
					ofName lineTo script(
						choiceName lineTo script(
							withName lineTo script("yes"),
							withName lineTo script("no")))),
			selectName lineTo script(
				"yes" lineTo script(literal(10)),
				"no" lineTo script(literal(20))))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						"boolean" lineTo expression(line(make("yes"))),
						line(
							select(
								"yes" caseTo 10.numberExpression,
								"no" caseTo 20.numberExpression)
						)),
					type(numberTypeLine)))
	}

	@Test
	fun isEqualTo() {
		script(
			line(literal(10)),
			isName lineTo script(equalName lineTo script(toName lineTo script(literal(20)))))
			.typedExpression
			.assertEqualTo(10.numberExpression.isEqualTo(20.numberExpression) of isType)
	}

	@Test
	fun isNotEqualTo() {
		script(
			line(literal(10)),
			isName lineTo script(notName lineTo script(equalName lineTo script(toName lineTo script(literal(20))))))
			.typedExpression
			.assertEqualTo(10.numberExpression.isEqualTo(20.numberExpression).negate of isType)
	}

	@Test
	fun defineType() {
		script(
			defineName lineTo script(
				typeName lineTo script(
					"color" lineTo script(
						choiceName lineTo script(
							withName lineTo script("red" lineTo script()),
							withName lineTo script("green" lineTo script()),
							withName lineTo script("blue" lineTo script()))))))
			.compiler.module.privateContext.types
			.assertEqualTo(
				types(type(
					"color" lineTo type(choice(
						"red" lineTo type(),
						"green" lineTo type(),
						"blue" lineTo type())))))
	}

	@Test
	fun defineFunction() {
		script(
			defineName lineTo script(
				functionName lineTo script(
					takingName lineTo script("foo"),
					doingName lineTo script("bar"))),
				"foo" lineTo script())
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						line(let(type("foo"), rhs(do_(doing(expression(line(make("bar")))))))),
						line(make("foo")),
						line(invoke(type("foo")))),
					type("bar")))
	}
}