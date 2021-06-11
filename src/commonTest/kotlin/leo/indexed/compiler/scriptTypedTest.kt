package leo.indexed.compiler

import leo.base.assertEqualTo
import leo.indexed.at
import leo.indexed.expression
import leo.indexed.tuple
import leo.indexed.typed.of
import leo.indexed.typed.tuple
import leo.indexed.typed.typed
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class ScriptTypedTest {
	@Test
	fun literals() {
		script(literal(10))
			.typed
			.assertEqualTo(expression<Unit>(literal(10)) of numberTypeLine)

		script(literal("foo"))
			.typed
			.assertEqualTo(expression<Unit>(literal("foo")) of textTypeLine)
	}

	@Test
	fun fields() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)))
			.typedTuple
			.assertEqualTo(
				tuple(
					typed(expression(literal(10)), "x" lineTo type(numberTypeLine)),
					typed(expression(literal(20)), "y" lineTo type(numberTypeLine))))
	}

	@Test
	fun make() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			"point" lineTo script())
			.typed
			.assertEqualTo(
				typed(
					expression(tuple(expression(literal(10)), expression(literal(20)))),
					"point" lineTo type(
						"x" lineTo type(numberTypeLine),
						"y" lineTo type(numberTypeLine))))
	}

	@Test
	fun get() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			"point" lineTo script(),
			"x" lineTo script())
			.typed
			.assertEqualTo(
				typed(
					expression(
						at(
							expression(tuple(expression(literal(10)), expression(literal(20)))),
							expression(0))),
					"x" lineTo type(numberTypeLine)))

		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			"point" lineTo script(),
			"y" lineTo script())
			.typed
			.assertEqualTo(
				typed(
					expression(
						at(
							expression(tuple(expression(literal(10)), expression(literal(20)))),
							expression(1))),
					"y" lineTo type(numberTypeLine)))
	}
}