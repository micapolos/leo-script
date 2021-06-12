package leo.named.compiler

import leo.base.assertEqualTo
import leo.doName
import leo.lineTo
import leo.named.expression.expression
import leo.named.expression.expressionTo
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.structure
import leo.named.typed.typed
import leo.script
import leo.type
import leo.typeStructure
import kotlin.test.Test

class CompileTest {
	@Test
	fun do_() {
		script(
			"foo" lineTo script(),
			doName lineTo script("bar"))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(
						function(
							typeStructure("foo"),
							"bar" expressionTo structure<Unit>()))
						.invoke(structure("foo" expressionTo structure())),
					"bar" lineTo type()))
	}
}