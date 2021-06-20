package leo.named.evaluator

import leo.base.assertEqualTo
import leo.named.expression.be
import leo.named.expression.expression
import leo.named.expression.let
import leo.named.expression.line
import leo.named.expression.numberExpression
import leo.named.expression.private
import leo.named.expression.rhs
import leo.named.value.numberValue
import leo.type
import kotlin.test.Test

class ExpressionModuleTest {

	@Test
	fun private() {
		expression(
			line(let(type("x"), rhs(be(10.numberExpression)))),
			line(private(expression(
				line(let(type("y"), rhs(be(20.numberExpression))))))))
			.module
			.assertEqualTo(
				module(
					private(
						dictionary(
							definition(type("x"), binding(10.numberValue)),
							definition(type("y"), binding(20.numberValue)))),
					public(
						dictionary(
							definition(type("x"), binding(10.numberValue))))))
	}
}

