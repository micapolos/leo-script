package leo.named.compiler

import leo.base.assertEqualTo
import leo.doName
import leo.lineTo
import leo.named.expression.expression
import leo.named.expression.expressionStructure
import leo.named.expression.expressionTo
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.structure
import leo.named.expression.variable
import leo.named.typed.typed
import leo.script
import leo.type
import leo.typeStructure
import kotlin.test.Test

class CompileTest {
	@Test
	fun do_withoutBindings() {
		script(
			"foo" lineTo script(),
			doName lineTo script("bar"))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						function(
							typeStructure("foo"),
							"bar" expressionTo expressionStructure<Unit>()))
						.invoke(structure("foo" expressionTo expressionStructure())),
					"bar" lineTo type()))
	}

	@Test
	fun do_withBindings() {
		script(
			"foo" lineTo script(),
			doName lineTo script("foo"))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						function(
							typeStructure("foo"),
							expression<Unit>(variable(typeStructure("foo")))))
						.invoke(structure("foo" expressionTo expressionStructure())),
					"foo" lineTo type()))
	}
}