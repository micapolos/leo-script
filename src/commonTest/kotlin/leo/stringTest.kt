package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class StringTest {
	@Test
	fun literalString() {
		"\"\n\"".literalString.assertEqualTo("\"\\\"\\n\\\"\"")
	}
}