package leo.expression.compiler

import leo.base.assertEqualTo
import leo.beName
import leo.bindName
import leo.commentName
import leo.exampleName
import leo.expression.applyBind
import leo.expression.expression
import leo.expression.expressionTo
import leo.expression.of
import leo.expression.op
import leo.expression.plus
import leo.expression.resolveEqual
import leo.expression.resolveMake
import leo.expression.structure
import leo.expression.variable
import leo.isName
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.noName
import leo.numberTypeLine
import leo.script
import leo.type
import leo.yesName
import kotlin.test.Test
import kotlin.test.assertFails

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
	fun be() {
		script(
			"ugly" lineTo script(),
			beName lineTo script("pretty"))
			.structure
			.assertEqualTo(structure("pretty" expressionTo structure()))
	}

	@Test
	fun comment() {
		script(
			"red" lineTo script(),
			commentName lineTo script(letName), // Empty "let" is syntax error, so compilation should succeed
			"color" lineTo script())
			.structure
			.assertEqualTo(structure("color" expressionTo structure("red")))
	}

	@Test
	fun example_compiles() {
		script(
			"red" lineTo script(),
			exampleName lineTo script("foo"),
			"color" lineTo script())
			.structure
			.assertEqualTo(structure("color" expressionTo structure("red")))
	}

	@Test
	fun example_fails() {
		// Empty "let" should produce compile-error.
		assertFails {
			script(
				"red" lineTo script(),
				exampleName lineTo script(letName),
				"color" lineTo script()
			)
				.structure
		}
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

	@Test
	fun isConstant() {
		script(isName lineTo script(yesName))
			.structure
			.assertEqualTo(true.structure)

		script(isName lineTo script(noName))
			.structure
			.assertEqualTo(false.structure)
	}

	@Test
	fun isExpression() {
		script(
			line(literal(10)),
			isName lineTo script(line(literal(20))))
			.structure
			.assertEqualTo(10.literal.expression.resolveEqual(20.literal.expression).structure)
	}

	@Test
	fun let() {
		// TODO: Wait until implemented
		assertFails {
			script(letName)
				.structure
				.assertEqualTo(null)
		}
	}
}