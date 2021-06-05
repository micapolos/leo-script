package scheme

import leo.base.assertEqualTo
import leo.invoke
import leo.lambda
import leo.term
import leo.text
import leo.varTerm
import kotlin.test.Test

class TermSchemeTest {
	@Test
	fun lambda() {
		lambda(lambda(varTerm<Scheme>(1).invoke(varTerm(0)))).scheme.string.assertEqualTo("(lambda v0 (lambda v1 (v0 v1)))")
		term("string-append".scheme).scheme.string.assertEqualTo("string-append")
		term("Hello, world!".text.scheme).scheme.string.assertEqualTo("\"Hello, world!\"")
		term(10.scheme).scheme.string.assertEqualTo("10")
		term(3.14.scheme).scheme.string.assertEqualTo("3.14")
		term(false.scheme).scheme.string.assertEqualTo("#f")
		term(true.scheme).scheme.string.assertEqualTo("#t")
	}
}