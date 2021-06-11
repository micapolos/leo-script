package leo.indexed.compiler

import leo.base.assertEqualTo
import leo.indexed.at
import leo.indexed.expression
import leo.indexed.typed.of
import leo.indexed.typed.tuple
import leo.indexed.variable
import leo.lineTo
import leo.numberTypeLine
import leo.type
import kotlin.test.Test

class TypedResolveTest {
	@Test
	fun resolveGet() {
		tuple(
			expression<Unit>(variable(123))
				.of(
					"x" lineTo type(
						"point" lineTo type(
							"x" lineTo type(numberTypeLine),
							"y" lineTo type(numberTypeLine)))))
			.resolveGetOrNull
			.assertEqualTo(
				tuple(expression(at(expression<Unit>(variable(123)), 0))
					.of("x" lineTo type(numberTypeLine))))
	}
}