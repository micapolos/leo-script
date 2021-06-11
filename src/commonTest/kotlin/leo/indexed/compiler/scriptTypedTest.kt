package leo.indexed.compiler

import leo.base.assertEqualTo
import leo.beName
import leo.doName
import leo.indexed.at
import leo.indexed.expression
import leo.indexed.function
import leo.indexed.invoke
import leo.indexed.tuple
import leo.indexed.typed.of
import leo.indexed.typed.tuple
import leo.indexed.typed.typed
import leo.indexed.variable
import leo.letName
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
import leo.textTypeLine
import leo.type
import leo.wordName
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
			.bodyTypedTuple
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

	@Test
	fun be() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			beName lineTo script("z" lineTo script(literal(30))))
			.typed
			.assertEqualTo(
				typed(
					expression(literal(30)),
					"z" lineTo type(numberTypeLine)))
	}

	@Test
	fun do_() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			doName lineTo script("x"))
			.typed
			.assertEqualTo(
				typed(
					expression(
						invoke(
							expression(function(2, expression(variable(1)))),
							tuple(expression(literal(10)), expression(literal(20))))),
					"x" lineTo type(numberTypeLine)))
	}

	@Test
	fun letBe() {
		script(
			letName lineTo script(
				"size" lineTo script(),
				beName lineTo script(literal(10))),
			"size" lineTo script())
			.typed
			.assertEqualTo(
				typed(
					expression(
						invoke(
							expression(function(1, expression(variable(0)))),
							tuple(expression(literal(10))))),
					numberTypeLine))
	}

	@Test
	fun let_indexing() {
		script(
			letName lineTo script(
				"size" lineTo script(),
				beName lineTo script(literal(10))),
			letName lineTo script(
				"name" lineTo script(),
				beName lineTo script(literal("foo"))),
			"size" lineTo script())
			.typed
			.assertEqualTo(
				typed(expression(
					invoke(
						expression(function(2, expression(variable(1)))),
						tuple(
							expression(literal(10)),
							expression(literal("foo"))))),
					numberTypeLine))

		script(
			letName lineTo script(
				"size" lineTo script(),
				beName lineTo script(literal(10))),
			letName lineTo script(
				"name" lineTo script(),
				beName lineTo script(literal("foo"))),
			"name" lineTo script())
			.typed
			.assertEqualTo(
				typed(expression(
					invoke(
						expression(function(2, expression(variable(0)))),
						tuple(
							expression(literal(10)),
							expression(literal("foo"))))),
					textTypeLine))
	}

	@Test
	fun word() {
		script(
			"red" lineTo script(),
			wordName lineTo script("color"))
			.bodyTypedTuple
			.assertEqualTo(
				tuple(
					expression<Unit>() of ("red" lineTo type()),
					expression<Unit>() of ("color" lineTo type())))
	}
}