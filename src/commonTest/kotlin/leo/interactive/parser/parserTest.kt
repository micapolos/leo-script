package leo.interactive.parser

import leo.base.assertEqualTo
import leo.interactive.begin
import leo.interactive.end
import leo.interactive.token
import leo.stack
import leo.stackLink
import kotlin.test.Test

class ParserTest {
	@Test
	fun beginSpace() {
		emptyPartialTokens
			.plusOrNull("foo ")
			.assertEqualTo(
				PartialTokens(
					Tokens(stack(token(begin("foo")))),
					IndentBodyPartialLine(
						IndentBody(
							emptyIndent,
							Body(EndTab(stackLink(end)), null)))))
	}

	@Test
	fun beginDot() {
		emptyPartialTokens
			.plusOrNull("foo.")
			.assertEqualTo(
				PartialTokens(
					Tokens(stack(
						token(begin("foo")),
						token(end))),
					emptyPartialLine))
	}

	@Test
	fun beginNewline() {
		emptyPartialTokens
			.plusOrNull("foo\n")
			.assertEqualTo(
				PartialTokens(
					Tokens(stack(token(begin("foo")))),
					emptyPartialLine))
	}
}