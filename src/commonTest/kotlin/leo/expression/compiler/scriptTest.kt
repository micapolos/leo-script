package leo.expression.compiler

import leo.base.assertEqualTo
import leo.expression.expression
import leo.expression.expressionTo
import leo.expression.plus
import leo.expression.resolveMake
import leo.expression.structure
import leo.lineTo
import leo.literal
import leo.script
import kotlin.test.Test

class ScriptTest {
	@Test
	fun empty() {
		script().structure.assertEqualTo(structure())
	}

	@Test
	fun name() {
		script("hello").structure.assertEqualTo(structure().resolveMake("hello"))
	}

	@Test
	fun make() {
		script(
			"red" lineTo script(),
			"color" lineTo script())
			.structure
			.assertEqualTo(structure().resolveMake("red").resolveMake("color"))
	}

	@Test
	fun field() {
		script("x" lineTo script(literal(10)))
			.structure
			.assertEqualTo(structure().plus("x" expressionTo 10.expression.structure))
	}

	@Test
	fun fields() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)))
			.structure
			.assertEqualTo(
				structure()
					.plus("x" expressionTo 10.expression.structure)
					.plus("y" expressionTo 20.expression.structure))
	}
}