package leo.term.compiler.scheme

import leo.base.assertEqualTo
import leo.line
import leo.lineTo
import leo.literal
import leo.plusName
import leo.script
import kotlin.test.Test

class SchemeTest {
	@Test
	fun literal() {
		script(line(literal("Hello, world!")))
			.scheme
			.string
			.assertEqualTo("\"Hello, world!\"")
	}

	@Test
	fun lines() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20)))
			.scheme
			.string
			.assertEqualTo("(((lambda (v0) (lambda (v1) (lambda (v2) ((v2 v0) v1)))) 10) 20)")
	}

	@Test
	fun numberPlusNumber() {
		script(
			line(literal(10)),
			plusName lineTo script(literal(20)))
			.scheme
			.string
			.assertEqualTo("((lambda (v0) (((lambda (x) (lambda (y) (+ x y))) (v0 (lambda (v1) (lambda (v2) v1)))) (v0 (lambda (v1) (lambda (v2) v2))))) (((lambda (v0) (lambda (v1) (lambda (v2) ((v2 v0) v1)))) 10) 20))")
	}
}