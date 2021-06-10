package leo.indexed.compiler

import leo.base.assertEqualTo
import leo.indexed.at
import leo.indexed.expression
import leo.indexed.typed.of
import leo.indexed.typed.tuple
import leo.indexed.typed.typedTo
import leo.lineTo
import leo.type
import kotlin.test.Test

class TypedResolveTest {
	@Test
	fun resolveGet() {
		val typed =
			"x" typedTo tuple<Unit>(
				"point" typedTo tuple(
					"x" typedTo tuple("zero" typedTo tuple()),
					"y" typedTo tuple("zero" typedTo tuple())))

		tuple(typed)
			.resolveGetOrNull
			.assertEqualTo(
				tuple(expression(at(typed.expression, expression(0))).of("x" lineTo type("zero" lineTo type()))))
	}
}