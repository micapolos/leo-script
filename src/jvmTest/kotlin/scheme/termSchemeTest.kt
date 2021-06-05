package leo

import leo.base.assertEqualTo
import scheme.Scheme
import scheme.scheme
import kotlin.test.Test

class TermSchemeTest {
	@Test
	fun lambda() {
		lambda(lambda(v<Scheme>(1)(v(0)))).scheme.string.assertEqualTo("(lambda v0 (lambda v1 (v0 v1)))")
		term("string-append".scheme).scheme.string.assertEqualTo("string-append")
	}
}