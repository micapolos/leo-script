package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TermSchemeStringTest {
	@Test
	fun lambda() {
		lambda(lambda(v<Scheme>(1)(v(0)))).schemeString.assertEqualTo("(lambda v0 (lambda v1 (v0 v1)))")
		term(scheme("string-append")).schemeString.assertEqualTo("string-append")
	}
}