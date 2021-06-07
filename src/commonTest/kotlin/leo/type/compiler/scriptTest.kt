package leo.type.compiler

import leo.base.assertEqualTo
import leo.lineTo
import leo.script
import leo.structure
import leo.type
import leo.typeStructure
import kotlin.test.Test

class ScriptTest {
	@Test
	fun empty() {
		script().typeStructure.assertEqualTo(typeStructure())
	}

	@Test
	fun name() {
		script("foo").typeStructure.assertEqualTo(structure("foo" lineTo type()))
	}

	@Test
	fun names() {
		script(
			"foo" lineTo script(),
			"bar" lineTo script())
			.typeStructure
			.assertEqualTo(structure("bar" lineTo type("foo" lineTo type())))
	}

	@Test
	fun field() {
		script("foo" lineTo script("bar"))
			.typeStructure
			.assertEqualTo(structure("foo" lineTo type("bar" lineTo type())))
	}

	@Test
	fun fields() {
		script(
			"foo" lineTo script("bar"),
			"zoo" lineTo script("zar"))
			.typeStructure
			.assertEqualTo(
				structure(
					"foo" lineTo type("bar" lineTo type()),
					"zoo" lineTo type("zar" lineTo type())))
	}

	@Test
	fun getField() {
		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")),
			"x" lineTo script())
			.typeStructure
			.assertEqualTo(structure("x" lineTo type("zero" lineTo type())))
	}
}