package scheme

import leo.base.assertEqualTo
import kotlin.test.Test

class EvalTest {
	@Test
	fun string() {
		"\"Hello, world!\"".scheme.eval.assertEqualTo("Hello, world!".scheme)
	}
}