package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class SyntaxTypedTest {
	@Test
	fun literals() {
		syntax(
			line(syntaxAtom(literal(10))),
			line(syntaxAtom(literal("Hello, world!"))))
			.typed
			.assertEqualTo(
				expression(
					line(expressionAtom(literal(10))),
					line(expressionAtom(literal("Hello, world!")))).of(
					type(
						numberTypeLine,
						textTypeLine)))
	}

	@Test
	fun fields() {
		syntax(
			"x" lineTo syntax("zero"),
			"y" lineTo syntax("one"))
			.typed
			.assertEqualTo(
				expression(
					line(
						atom(
							"x" fieldTo expression(
								line(atom("zero" fieldTo emptyTyped)))
								.of(type("zero" lineTo type())))),
					line(
						atom(
							"y" fieldTo expression(
								line(atom("one" fieldTo emptyTyped)))
								.of(type("one" lineTo type())))))
					.of(
						type(
							"x" lineTo type("zero" lineTo type()),
							"y" lineTo type("one" lineTo type()))))
	}
}