package leo.named.evaluator

import leo.base.assertEqualTo
import leo.named.expression.be
import leo.named.expression.do_
import leo.named.expression.doing
import leo.named.expression.expression
import leo.named.expression.let
import leo.named.expression.line
import leo.named.expression.numberExpression
import leo.named.expression.rhs
import leo.named.value.function
import leo.named.value.numberValue
import leo.type
import kotlin.test.Test

class ExpressionDictionaryTest {
	@Test
	fun letBe() {
		expression(line(let(type("x"), rhs(be(10.numberExpression)))))
			.dictionary
			.assertEqualTo(
				dictionary(definition(type("x"), binding(10.numberValue)))
			)
	}

	@Test
	fun letDo() {
		expression(line(let(type("y"), rhs(do_(doing(10.numberExpression))))))
			.dictionary
			.assertEqualTo(
				dictionary(
					definition(type("y"), binding(function(dictionary(), doing(10.numberExpression))))
				)
			)
	}
}