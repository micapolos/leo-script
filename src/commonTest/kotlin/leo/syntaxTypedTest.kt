package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class SyntaxTypedTest {
	@Test
	fun literal() {
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
}