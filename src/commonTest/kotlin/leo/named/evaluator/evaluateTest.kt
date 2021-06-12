package leo.named.evaluator

import leo.base.assertEqualTo
import leo.literal
import leo.named.expression.expression
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.variable
import leo.named.value.structure
import leo.named.value.value
import leo.named.value.valueTo
import leo.numberName
import leo.numberTypeLine
import leo.typeStructure
import kotlin.test.Test

class EvaluateTest {
	@Test
	fun invoke_static() {
		line(
			function<Unit>(
				typeStructure(numberTypeLine),
				"foo" lineTo expression()
			))
			.invoke(expression(line(literal(10))))
			.evaluate
			.assertEqualTo("foo" valueTo structure())
	}

	@Test
	fun invoke_variable() {
		line(
			function<Unit>(
				typeStructure(numberTypeLine),
				"foo" lineTo expression(line(variable(typeStructure(numberName))))
			))
			.invoke(expression(line(literal(10))))
			.evaluate
			.assertEqualTo("foo" valueTo structure(value(literal(10))))
	}
}