package leo.named.evaluator

import leo.base.assertEqualTo
import leo.givenName
import leo.lineTo
import leo.literal
import leo.named.expression.be
import leo.named.expression.bind
import leo.named.expression.caseTo
import leo.named.expression.do_
import leo.named.expression.doing
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.function
import leo.named.expression.get
import leo.named.expression.give
import leo.named.expression.invoke
import leo.named.expression.let
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.make
import leo.named.expression.numberExpression
import leo.named.expression.numberExpressionLine
import leo.named.expression.private
import leo.named.expression.recursive
import leo.named.expression.rhs
import leo.named.expression.take
import leo.named.expression.textExpression
import leo.named.expression.with
import leo.named.value.double
import leo.named.value.function
import leo.named.value.get
import leo.named.value.line
import leo.named.value.lineTo
import leo.named.value.make
import leo.named.value.numberValue
import leo.named.value.textValue
import leo.named.value.value
import leo.natives.minusName
import leo.numberName
import leo.numberTypeLine
import leo.plusName
import leo.textName
import leo.type
import kotlin.test.Test
import kotlin.test.assertFailsWith

class EvaluateTest {
	@Test
	fun empty() {
		expression().value.assertEqualTo(value())
	}

	@Test
	fun literals() {
		10.numberExpression.value.assertEqualTo(10.numberValue)
		"foo".textExpression.value.assertEqualTo("foo".textValue)
	}

	@Test
	fun fields() {
		expression(
			"x" lineTo 10.numberExpression,
			"y" lineTo 20.numberExpression)
			.value
			.assertEqualTo(
				value(
					"x" lineTo 10.numberValue,
					"y" lineTo 20.numberValue))
	}

	@Test
	fun getEvaluation() {
		expression(
			"point" lineTo expression(
				"x" lineTo 10.numberExpression,
				"y" lineTo 20.numberExpression),
			line(get("x")))
			.value
			.assertEqualTo(value("x" lineTo 10.numberValue))

		expression(
			"point" lineTo expression(
				"x" lineTo 10.numberExpression,
				"y" lineTo 20.numberExpression),
			line(get("y")))
			.value
			.assertEqualTo(value("y" lineTo 20.numberValue))
	}

	@Test
	fun beEvaluate() {
		expression(
			"foo" lineTo expression(),
			line(be(expression("bar"))))
			.value
			.assertEqualTo(value("bar"))
	}

	@Test
	fun doEvaluate_withoutBindings() {
		expression(
			"x" lineTo expression(expressionLine(literal(10))),
			"y" lineTo expression(expressionLine(literal(20))),
			line(do_(doing(expression("z" lineTo expression())))))
			.value
			.assertEqualTo(value("z"))
	}

	@Test
	fun doEvaluate_given() {
		expression(
			"x" lineTo expression(expressionLine(literal(10))),
			"y" lineTo expression(expressionLine(literal(20))),
			line(do_(doing(expression(line(invoke(type(givenName))))))))
			.value
			.assertEqualTo(
				value(
					givenName lineTo value(
						"x" lineTo 10.numberValue,
						"y" lineTo 20.numberValue)))
	}

	@Test
	fun doEvaluate_withBindings() {
		expression(
			"x" lineTo expression(expressionLine(literal(10))),
			"y" lineTo expression(expressionLine(literal(20))),
			line(do_(doing(expression(line(invoke(type("x"))))))))
			.value
			.assertEqualTo(value("x" lineTo 10.numberValue))
	}

	@Test
	fun doNative() {
		expression(
			30.numberExpressionLine,
			minusName lineTo 20.numberExpression,
			line(do_(doing {
				get(numberName).double
					.minus(get(minusName).get(numberName).double)
					.numberValue
			})))
			.value
			.assertEqualTo(10.numberValue)
	}

	@Test
	fun bindEvaluate() {
		expression(
			line(bind(expression(
				"x" lineTo expression(expressionLine(literal(10))),
				"y" lineTo expression(expressionLine(literal(20)))))),
			line(invoke(type("x"))))
			.value
			.assertEqualTo(value("x" lineTo 10.numberValue))

		expression(
			line(bind(expression(
				"x" lineTo expression(expressionLine(literal(10))),
				"y" lineTo expression(expressionLine(literal(20)))))),
			line(invoke(type("y"))))
			.value
			.assertEqualTo(value("y" lineTo 20.numberValue))
	}

	@Test
	fun doing() {
		expression(line(function(type(numberTypeLine), doing(expression("foo")))))
			.value
			.assertEqualTo(value(line(function(dictionary(), doing(expression("foo"))))))
	}

	@Test
	fun doingGive_withoutInvoke() {
		expression(
			line(function(type(numberTypeLine), doing(expression("foo")))),
			line(give(10.numberExpression)))
			.value
			.assertEqualTo(value("foo"))
	}

	@Test
	fun doingGive_withInvoke() {
		expression(
			type(numberTypeLine) lineTo doing(expression(line(invoke(type(numberName))))),
			line(give(expression(expressionLine(literal(10))))))
			.value
			.assertEqualTo(10.numberValue)
	}

	@Test
	fun doingGive_native() {
		expression(
			type(numberTypeLine) lineTo doing { get(numberName).double.plus(1).numberValue },
			line(give(expression(expressionLine(literal(10))))))
			.value
			.assertEqualTo(11.numberValue)
	}

	@Test
	fun with() {
		expression(
			"x" lineTo 10.numberExpression,
			line(with(expression(
				"y" lineTo 20.numberExpression,
				"z" lineTo 30.numberExpression))))
			.value
			.assertEqualTo(
				value(
					"x" lineTo 10.numberValue,
					"y" lineTo 20.numberValue,
					"z" lineTo 30.numberValue))
	}

	@Test
	fun letBe() {
		expression(
			line(
				let(
					type("x"),
					rhs(be(10.numberExpression)))),
			line(invoke(type("x"))))
			.value
			.assertEqualTo(10.numberValue)
	}

	@Test
	fun letDo() {
		expression(
			line(
				let(
					type("x" lineTo type(numberTypeLine)),
					rhs(do_(
						doing(expression(
							line(invoke(type("x"))),
							line(get(numberName))))
					)))),
			"x" lineTo 10.numberExpression,
			line(invoke(type("x" lineTo type(numberTypeLine)))))
			.value
			.assertEqualTo(10.numberValue)
	}

	@Test
	fun make() {
		expression("foo").make("bar")
			.value
			.assertEqualTo(value("foo").make("bar"))
	}

	@Test
	fun switch() {
		expression(
			"id" lineTo 10.numberExpression,
			line(
				leo.named.expression.switch(
					numberName caseTo expression(line(invoke(type(numberName)))),
					textName caseTo expression(line(invoke(type(textName)))))))
			.value
			.assertEqualTo(10.numberValue)

		expression(
			"id" lineTo "foo".textExpression,
			line(
				leo.named.expression.switch(
					numberName caseTo expression(line(invoke(type(numberName)))),
					textName caseTo expression(line(invoke(type(textName)))))))
			.value
			.assertEqualTo("foo".textValue)
	}

	@Test
	fun privateValue() {
		expression(
			line(private(expression(
				line(let(type("x"), rhs(be(10.numberExpression))))))),
			line(invoke(type("x"))))
			.value
			.assertEqualTo(10.numberValue)
	}

	@Test
	fun recursiveStackOverflow() {
		assertFailsWith<StackOverflowError> {
			expression(
				line(recursive(expression(
					line(let(type("ping"), rhs(do_(doing(expression(line(invoke(type("ping")))))))))))),
				line(invoke(type("ping"))))
				.value
		}
	}

	@Test
	fun take() {
		expression(
			10.numberExpressionLine,
			line(take(expression(
				type(numberTypeLine) lineTo doing(expression(line(invoke(type(givenName)))))
			))))
			.value
			.assertEqualTo(value(givenName lineTo 10.numberValue))
	}

	@Test
	fun predule() {
		preludeDictionary
			.value(
				expression(
					10.numberExpressionLine,
					plusName lineTo expression(20.numberExpressionLine),
					line(invoke(type(numberTypeLine, plusName lineTo type(numberTypeLine))))))
			.assertEqualTo(30.numberValue)
	}
}