package leo.definition

import leo.base.assertEqualTo
import leo.expression.compiler
import leo.expression.expression
import leo.expression.expressionTo
import leo.expression.invoke
import leo.expression.kotlinCompilation
import leo.expression.of
import leo.expression.op
import leo.expression.resolveGet
import leo.expression.structure
import leo.expression.variable
import leo.get
import leo.lineTo
import leo.literal
import leo.numberName
import leo.numberTypeLine
import leo.type
import kotlin.test.Test

class DefinitionKotlinTest {
	@Test
	fun definition() {
		Definition(
			type("increment" lineTo type(numberTypeLine)),
			Binding(
				structure(
					"increment".variable.op.of("increment" lineTo type(numberTypeLine))
						.resolveGet(numberName),
					"plus" expressionTo structure(1.literal.expression))
					.invoke.op.of(numberTypeLine),
				isFunction = true))
			.kotlinCompilation
			.get(compiler())
			.string
			.assertEqualTo("fun _invoke(increment: DoubleIncrement) = _invoke(increment.number, plus(1))")
	}
}