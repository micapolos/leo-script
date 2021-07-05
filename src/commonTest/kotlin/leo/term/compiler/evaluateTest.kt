package leo.term.compiler

import leo.base.assertEqualTo
import leo.doName
import leo.getName
import leo.line
import leo.lineTo
import leo.literal
import leo.makeName
import leo.numberName
import leo.numberTypeLine
import leo.script
import leo.term.anyValue
import leo.term.typed.typed
import leo.type
import kotlin.test.Test

class EvaluateTest {
	@Test
	fun do_() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			doName lineTo script(
				getName lineTo script("x")))
			.typedValue
			.assertEqualTo(
				typed(
					10.0.anyValue,
					type("x" lineTo type(numberTypeLine))))
	}

	@Test
	fun get() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))
			),
			getName lineTo script("x"))
			.typedValue
			.assertEqualTo(
				typed(
					10.0.anyValue,
					type("x" lineTo type(numberTypeLine))))
	}

	@Test
	fun getNumber() {
		script(
			"id" lineTo script(line(literal(10))),
			getName lineTo script(numberName))
			.typedValue
			.assertEqualTo(
				typed(
					10.0.anyValue,
					type(numberTypeLine)))
	}

	@Test
	fun make() {
		script(
			line(literal(10)),
			makeName lineTo script("id"))
			.typedValue
			.assertEqualTo(
				typed(
					10.0.anyValue,
					type("id" lineTo type(numberTypeLine))))

	}

	@Test
	fun numberAddNumber() {
		script(
			line(literal(10)),
			"add" lineTo script(literal(20)))
			.typedValue
			.assertEqualTo(
				typed(
					30.0.anyValue,
					type(numberTypeLine)))
	}

}