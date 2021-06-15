package leo.named.evaluator

import leo.base.assertEqualTo
import leo.lineTo
import leo.literal
import leo.named.expression.binding
import leo.named.expression.body
import leo.named.expression.caseTo
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.function
import leo.named.expression.get
import leo.named.expression.in_
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.switch
import leo.named.expression.variable
import leo.named.value.double
import leo.named.value.function
import leo.named.value.lineTo
import leo.named.value.numberValue
import leo.named.value.textValue
import leo.named.value.value
import leo.numberName
import leo.numberTypeLine
import leo.textName
import leo.type
import kotlin.test.Test

class EvaluateTest {
	@Test
	fun invoke_static() {
		expression(line(
			function(
				body(expression("foo" lineTo expression()))
			)))
			.invoke(expression(expressionLine(literal(10))))
			.evaluate
			.assertEqualTo(value("foo" lineTo value()))
	}

	@Test
	fun invoke_variable() {
		expression(line(
			function(
				body(expression("foo" lineTo expression(variable(type(numberName)))))
			)))
			.invoke(expression(expressionLine(literal(10))))
			.evaluate
			.assertEqualTo(value("foo" lineTo 10.numberValue))
	}

	@Test
	fun invoke_native() {
		function("numberIncrement") { get(numberName).double.plus(1).numberValue }
			.invoke(expression(expressionLine(literal(10))))
			.evaluate
			.assertEqualTo(11.numberValue)
	}

	@Test
	fun bind() {
		binding(
			type("ping" lineTo type(numberTypeLine)),
			expression("ping" lineTo expression(expressionLine(literal(10)))))
			.in_(expression("pong" lineTo get(type("ping" lineTo type(numberTypeLine))).get(numberName)))
			.evaluate
			.assertEqualTo(value("pong" lineTo 10.numberValue))
	}

	@Test
	fun switch() {
		expression(
			"color" lineTo expression(
				"red" lineTo expression(
					expressionLine(literal(10)))))
			.switch(
				"red" caseTo get(type("red")).get(numberName),
				"blue" caseTo get(type("blue")).get(textName))
			.evaluate
			.assertEqualTo(10.numberValue)

		expression(
			"color" lineTo expression(
				"blue" lineTo expression(
					expressionLine(literal("foo")))))
			.switch(
				"red" caseTo get(type("red")).get(numberName),
				"blue" caseTo get(type("blue")).get(textName))
			.evaluate
			.assertEqualTo("foo".textValue)
	}

	@Test
	fun recursiveDictionary() {
		val baseDictionary = dictionary(definition(type("foo"), binding(value("bar"))))

		val pingDefinition = definition(
			type("ping"),
			binding(function(baseDictionary, body(get(type("pong"))))))
		val pingDictionary = baseDictionary.plus(pingDefinition)

		val pongDefinition = definition(
			type("pong"),
			binding(function(pingDictionary, body(get(type("ping"))))))

		val finalDictionary = baseDictionary
			.plusRecursive(dictionary(pingDefinition, pongDefinition))

		finalDictionary
			.value(type("ping"))
	}
}