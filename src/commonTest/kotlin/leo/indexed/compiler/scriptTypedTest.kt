package leo.indexed.compiler

import leo.base.assertEqualTo
import leo.indexed.at
import leo.indexed.expression
import leo.indexed.tuple
import leo.indexed.typed.of
import leo.indexed.typed.tuple
import leo.indexed.typed.typed
import leo.indexed.typed.typedTo
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
import leo.type
import kotlin.test.Test

class ScriptTypedTest {
	@Test
	fun literals() {
		script(literal(10))
			.typedTuple
			.assertEqualTo(tuple(typed(literal(10))))
	}

	@Test
	fun fields() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)))
			.typedTuple
			.assertEqualTo(
				tuple(
					"x" typedTo tuple(typed(literal(10))),
					"y" typedTo tuple(typed(literal(20)))))
	}

	@Test
	fun make() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			"point" lineTo script())
			.typedTuple
			.assertEqualTo(
				tuple(
					"point" typedTo tuple(
						"x" typedTo tuple(typed(literal(10))),
						"y" typedTo tuple(typed(literal(20))))))
	}

	@Test
	fun get() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			"point" lineTo script(),
			"x" lineTo script())
			.typedTuple
			.assertEqualTo(
				tuple(
					expression(
						at(
							expression(tuple(expression(literal(10)), expression(literal(20)))),
							expression<Unit>(0))).of("x" lineTo type(numberTypeLine))))
	}
}