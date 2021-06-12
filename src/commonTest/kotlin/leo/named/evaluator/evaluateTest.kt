package leo.named.evaluator

import leo.base.assertEqualTo
import leo.literal
import leo.named.expression.expression
import leo.named.expression.expressionTo
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.structure
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
		expression(
			function<Unit>(
				typeStructure(numberTypeLine),
				"foo" expressionTo leo.named.expression.structure()))
			.invoke(structure(expression(literal(10))))
			.evaluate
			.assertEqualTo("foo" valueTo leo.named.value.structure())
	}

	@Test
	fun invoke_variable() {
		expression(
			function<Unit>(
				typeStructure(numberTypeLine),
				"foo" expressionTo structure(expression(variable(typeStructure(numberName))))))
			.invoke(structure(expression(literal(10))))
			.evaluate
			.assertEqualTo("foo" valueTo structure(value(literal(10))))
	}
}