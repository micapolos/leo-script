package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ExpressionCompilationTest {
	@Test
	fun literal() {
		script(line(literal("Hello, world!")))
			.expression
			.assertEqualTo(expression(op(literal("Hello, world!"))))

		script(line(literal(123)))
			.expression
			.assertEqualTo(expression(op(literal(123))))
	}

	@Test
	fun switch() {
		script(
			line(literal("Hello, world!")),
			switchName lineTo script(
				textName lineTo script(line(literal("text"))),
				numberName lineTo script(line(literal("number")))))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, world!")),
					op(switch(
						textName caseTo expression(op(literal("text"))),
						numberName caseTo expression(op(literal("number")))))))
	}
}