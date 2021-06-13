package leo.named.evaluator

import leo.base.assertEqualTo
import leo.literal
import leo.named.expression.body
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.variable
import leo.named.value.lineTo
import leo.named.value.numberPlusNumber
import leo.named.value.numberValue
import leo.named.value.value
import leo.named.value.valueLine
import leo.numberName
import leo.numberTypeLine
import leo.type
import kotlin.test.Test

class EvaluateTest {
	@Test
	fun invoke_static() {
		expression(line(
			function(
				type(numberTypeLine),
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
				type(numberTypeLine),
				body(expression("foo" lineTo expression(variable(type(numberName)))))
			)))
			.invoke(expression(expressionLine(literal(10))))
			.evaluate
			.assertEqualTo(value("foo" lineTo value(valueLine(literal(10)))))
	}

	@Test
	fun invoke_native() {
		expression(line(
			function(
				type(numberTypeLine),
				body { dictionary -> dictionary.value(type(numberName)).numberPlusNumber(1.0.numberValue) })))
			.invoke(expression(expressionLine(literal(10))))
			.evaluate
			.assertEqualTo(11.numberValue)
	}
}