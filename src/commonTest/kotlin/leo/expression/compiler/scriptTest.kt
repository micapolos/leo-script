package leo.expression.compiler

import leo.base.assertEqualTo
import leo.bindName
import leo.expression.applyBind
import leo.expression.expressionTo
import leo.expression.of
import leo.expression.op
import leo.expression.plus
import leo.expression.resolveMake
import leo.expression.structure
import leo.expression.variable
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
import leo.type
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
			.assertEqualTo(structure().plus("x" expressionTo 10.literal.structure))
	}

	@Test
	fun fields() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)))
			.structure
			.assertEqualTo(
				structure()
					.plus("x" expressionTo 10.literal.structure)
					.plus("y" expressionTo 20.literal.structure))
	}

	@Test
	fun bind_getFirst() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			bindName lineTo script("x"))
			.structure
			.assertEqualTo(
				structure()
					.plus("x" expressionTo 10.literal.structure)
					.plus("y" expressionTo 20.literal.structure)
					.applyBind("x".variable.op of ("x" lineTo type(numberTypeLine))))
	}

	@Test
	fun bind_getSecond() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			bindName lineTo script("y"))
			.structure
			.assertEqualTo(
				structure()
					.plus("x" expressionTo 10.literal.structure)
					.plus("y" expressionTo 20.literal.structure)
					.applyBind("y".variable.op of ("y" lineTo type(numberTypeLine))))
	}

	@Test
	fun bind_missing() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)),
			bindName lineTo script("z"))
			.structure
			.assertEqualTo(
				structure()
					.plus("x" expressionTo 10.literal.structure)
					.plus("y" expressionTo 20.literal.structure)
					.applyBind("z" expressionTo structure()))
	}
}