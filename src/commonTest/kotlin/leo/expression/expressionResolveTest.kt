package leo.expression

import leo.base.assertEqualTo
import leo.base.assertNull
import leo.isTypeLine
import leo.literal
import leo.negateName
import kotlin.test.Test

class ExpressionResolveTest {
	@Test
	fun boolean() {
		true.expression.booleanOrNull.assertEqualTo(true)
		false.expression.booleanOrNull.assertEqualTo(false)
		10.literal.expression.booleanOrNull.assertNull
	}

	@Test
	fun booleanNegate() {
		negateName
			.expressionTo(structure("x".variable.op.of(isTypeLine)))
			.resolveNegatedOrNull
			.assertEqualTo("x".variable.op of isTypeLine)

		negateName
			.expressionTo(10.literal.structure)
			.resolveNegatedOrNull
			.assertNull
	}
}