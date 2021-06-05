package leo.expression.compiler

import leo.base.assertEqualTo
import leo.expression.expression
import leo.expression.vector
import leo.line
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.syntax
import leo.syntaxAtom
import leo.textTypeLine
import leo.type
import leo.typeStructure
import kotlin.test.Test

class SyntaxCompiledTest {
	@Test
	fun empty() {
		syntax()
			.compiledVector
			.assertEqualTo(vector().of(typeStructure()))
	}

	@Test
	fun singletonVector() {
		syntax(
			line(syntaxAtom(literal(123))))
			.compiledVector
			.assertEqualTo(
				vector(
					expression(literal(123)))
					.of(
						typeStructure(
							textTypeLine,
							numberTypeLine)))
	}

	@Test
	fun literals() {
		syntax(
			line(syntaxAtom(literal("Hello, world!"))),
			line(syntaxAtom(literal(123))))
			.compiledVector
			.assertEqualTo(
				vector(
					expression(literal("Hello, world!")),
					expression(literal(123)))
					.of(
						typeStructure(
							textTypeLine,
							numberTypeLine)))
	}

	@Test
	fun fields() {
		syntax(
			"x" lineTo syntax(line(syntaxAtom(literal(10)))),
			"y" lineTo syntax(line(syntaxAtom(literal(20)))))
			.compiledVector
			.assertEqualTo(
				vector(
					expression(literal(10)),
					expression(literal(20)))
					.of(
						typeStructure(
							"x" lineTo type(numberTypeLine),
							"y" lineTo type(numberTypeLine))))
	}
}
