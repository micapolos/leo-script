package leo.type.compiler

import leo.base.assertEqualTo
import leo.lineTo
import leo.numberName
import leo.numberTypeLine
import leo.script
import leo.textName
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class ScriptTest {
	@Test
	fun empty() {
		script().type.assertEqualTo(type())
	}

	@Test
	fun name() {
		script("foo").type.assertEqualTo(type("foo" lineTo type()))
	}

	@Test
	fun names() {
		script(
			"foo" lineTo script(),
			"bar" lineTo script())
			.type
			.assertEqualTo(type("bar" lineTo type("foo" lineTo type())))
	}

	@Test
	fun field() {
		script("foo" lineTo script("bar"))
			.type
			.assertEqualTo(type("foo" lineTo type("bar" lineTo type())))
	}

	@Test
	fun fields() {
		script(
			"foo" lineTo script("bar"),
			"zoo" lineTo script("zar"))
			.type
			.assertEqualTo(
				type(
					"foo" lineTo type("bar" lineTo type()),
					"zoo" lineTo type("zar" lineTo type()))
			)
	}

	@Test
	fun getField() {
		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")),
			"x" lineTo script())
			.type
			.assertEqualTo(type("x" lineTo type("zero" lineTo type())))

		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")),
			"y" lineTo script())
			.type
			.assertEqualTo(type("y" lineTo type("one" lineTo type())))
	}

	@Test
	fun literal() {
		script(textName)
			.type
			.assertEqualTo(type(textTypeLine))

		script(numberName)
			.type
			.assertEqualTo(type(numberTypeLine))
	}
}