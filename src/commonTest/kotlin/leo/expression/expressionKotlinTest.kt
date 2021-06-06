package leo.expression

import leo.base.assertEqualTo
import leo.numberTypeLine
import kotlin.test.Test

class ExpressionKotlinTest {
	@Test
	fun get() {
		"foo".expression
			.get("length").op.of(numberTypeLine)
			.kotlin.string
			.assertEqualTo("\"foo\".length")
	}
}