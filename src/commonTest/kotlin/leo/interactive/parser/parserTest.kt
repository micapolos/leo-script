package leo.interactive.parser

import leo.base.assertEqualTo
import leo.interactive.begin
import leo.interactive.end
import leo.interactive.token
import leo.literal
import kotlin.test.Test

class ParserTest {
	@Test
	fun letter() {
		tokensPrefix()
			.plusOrNull("f")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(word(letter('f')))))))
			)
	}

	@Test
	fun digit() {
		tokensPrefix()
			.plusOrNull("2")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(numberPrefix(number(digit(2)))))))))
			)
	}

	@Test
	fun digitDigit() {
		tokensPrefix()
			.plusOrNull("23")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(numberPrefix(number(digit(2), digit(3)))))))))
			)
	}

	@Test
	fun minus() {
		tokensPrefix()
			.plusOrNull("-")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(numberPrefix(numberNegative)))))))
			)
	}

	@Test
	fun minusDigit() {
		tokensPrefix()
			.plusOrNull("-2")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(numberPrefix(negative(number(digit(2))))))))))
			)
	}

	@Test
	fun quote() {
		tokensPrefix()
			.plusOrNull("\"")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(textPrefix(textOpening())))))))
			)
	}

	@Test
	fun quoteChar() {
		tokensPrefix()
			.plusOrNull("\"f")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(textPrefix(textOpening(textItem('f')))))))))
			)
	}

	@Test
	fun quoteEscape() {
		tokensPrefix()
			.plusOrNull("\"\\")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(textPrefix(textOpening().with(textItemPrefix(escape)))))))))
			)
	}

	@Test
	fun quoteEscapeNewline() {
		tokensPrefix()
			.plusOrNull("\"\\n")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(textPrefix(textOpening(textItem(escaped('n'))))))))))
			)
	}

	@Test
	fun quoteCharChar() {
		tokensPrefix()
			.plusOrNull("\"fo")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(textPrefix(textOpening(textItem('f'), textItem('o')))))))))
			)
	}

	@Test
	fun textLiteral() {
		tokensPrefix()
			.plusOrNull("\"foo\"")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(literalPrefix(textPrefix("foo")))))))
			)
	}

	@Test
	fun numberLiteralDot() {
		tokensPrefix()
			.plusOrNull("123.")
			.assertEqualTo(
				prefix(
					tokens(token(literal(123))),
					line(body(indent(), spaced())))
			)
	}

	@Test
	fun textLiteralDot() {
		tokensPrefix()
			.plusOrNull("\"foo\".")
			.assertEqualTo(
				prefix(
					tokens(token(literal("foo"))),
					line(body(indent(), spaced())))
			)
	}

	@Test
	fun letterLetter() {
		tokensPrefix()
			.plusOrNull("fo")
			.assertEqualTo(
				prefix(
					tokens(),
					line(
						body(
							indent(),
							spaced(
								null,
								atomPrefix(word(letter('f'), letter('o')))))))
			)
	}

	@Test
	fun wordSpace() {
		tokensPrefix()
			.plusOrNull("foo ")
			.assertEqualTo(
				prefix(
					tokens(token(begin("foo"))),
					line(
						body(
							indent(),
							spaced(tab(end), null))))
			)
	}

	@Test
	fun nameSpaceNameSpace() {
		tokensPrefix()
			.plusOrNull("foo bar ")
			.assertEqualTo(
				prefix(
					tokens(
						token(begin("foo")),
						token(begin("bar"))),
					line(
						body(
							indent(),
							spaced(tab(end, end), null))))
			)
	}

	@Test
	fun wordDot() {
		tokensPrefix()
			.plusOrNull("foo.")
			.assertEqualTo(
				prefix(
					tokens(
						token(begin("foo")),
						token(end)),
					line(body(indent(), spaced())))
			)
	}

	@Test
	fun wordDotWordDot() {
		tokensPrefix()
			.plusOrNull("foo.bar.")
			.assertEqualTo(
				prefix(
					tokens(
						token(begin("foo")),
						token(end),
						token(begin("bar")),
						token(end)),
					line(body(indent(), spaced())))
			)
	}

	@Test
	fun wordNewline() {
		tokensPrefix()
			.plusOrNull("foo\n")
			.assertEqualTo(
				prefix(
					tokens(token(begin("foo"))),
					line(
						header(
							prefix(indent(), null),
							suffix(indent(tab(end))))))
			)
	}

	@Test
	fun wordNewlineSpace() {
		tokensPrefix()
			.plusOrNull("foo\n ")
			.assertEqualTo(
				prefix(
					tokens(
						token(begin("foo"))),
					line(
						header(
							prefix(indent(), prefix(tab(end))),
							suffix(indent()))))
			)
	}

	@Test
	fun nameNewlineTab() {
		tokensPrefix()
			.plusOrNull("foo\n  ")
			.assertEqualTo(
				prefix(
					tokens(token(begin("foo"))),
					line(
						header(
							prefix(indent(tab(end)), null),
							suffix(indent())))))
	}

	@Test
	fun nameNewlineTabLetter() {
		tokensPrefix()
			.plusOrNull("foo\n  b")
			.assertEqualTo(
				prefix(
					tokens(token(begin("foo"))),
					line(
						body(
							indent(tab(end)),
							spaced(null, atomPrefix(word(letter('b'))))))))
	}

	@Test
	fun spacedNames() {
		tokensPrefix()
			.plusOrNull("foo bar zoo\n")
			?.endTokensOrNull
			.assertEqualTo(
				tokens(
					token(begin("foo")),
					token(begin("bar")),
					token(begin("zoo")),
					token(end),
					token(end),
					token(end)))
	}

	@Test
	fun spacedNamesLiteral() {
		tokensPrefix()
			.plusOrNull("foo bar 10")
			.assertEqualTo(
				prefix(
					tokens(
						token(begin("foo")),
						token(begin("bar"))),
					line(
						body(
							indent(),
							spaced(
								tab(end, end),
								atomPrefix(
									literalPrefix(
										numberPrefix(
											number(
												digit(1),
												digit(0))))))))))
	}

	@Test
	fun spacedNamesLiteralNewline() {
		tokensPrefix()
			.plusOrNull("foo bar 10\n")
			?.endTokensOrNull
			.assertEqualTo(
				tokens(
					token(begin("foo")),
					token(begin("bar")),
					token(literal(10)),
					token(end),
					token(end)))
	}

	@Test
	fun structure() {
		tokensPrefix()
			.plusOrNull("point\n  x 10\n  y 20\n")
			?.endTokensOrNull
			.assertEqualTo(null)
	}
}