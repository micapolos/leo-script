package leo25.base

import kotlin.test.Test

class StringTest {
	@Test
	fun indentNewlines() {
		"foo\nbar\nzoo".indentNewlines(1).assertEqualTo("foo\n  bar\n  zoo")
	}
}