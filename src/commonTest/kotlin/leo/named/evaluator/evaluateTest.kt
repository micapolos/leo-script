package leo.named.evaluator

import leo.base.assertEqualTo
import leo.literal
import leo.named.expression.expressionStructure
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.expression.lineTo
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
		line(
			function<Unit>(
				typeStructure(numberTypeLine),
				"foo" lineTo expressionStructure()
			))
			.invoke(structure(line(literal(10))))
			.evaluate
			.assertEqualTo("foo" valueTo leo.named.value.structure())
	}

	@Test
	fun invoke_variable() {
		line(
			function<Unit>(
				typeStructure(numberTypeLine),
				"foo" lineTo structure(line(variable(typeStructure(numberName))))))
			.invoke(structure(line(literal(10))))
			.evaluate
			.assertEqualTo("foo" valueTo structure(value(literal(10))))
	}
}