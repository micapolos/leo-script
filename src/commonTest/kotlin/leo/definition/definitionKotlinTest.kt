package leo.expression

import leo.base.assertEqualTo
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
						.get(numberName).op.of(numberTypeLine),
					"plus" expressionTo structure(1.literal.expression))
					.invoke.op.of(numberTypeLine),
				isFunction = true))
			.kotlinCompilation
			.get(compiler())
			.string
			.assertEqualTo("fun _invoke(increment: DoubleIncrement) = _invoke(increment.number, plus(1))")
	}
}