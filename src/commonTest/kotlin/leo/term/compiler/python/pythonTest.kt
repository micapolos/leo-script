package leo.term.compiler.python

import leo.base.assertEqualTo
import leo.line
import leo.lineTo
import leo.plusName
import leo.script
import org.junit.Test

class PythonTest {
	@Test
	fun literal() {
		script(line(leo.literal("Hello, world!")))
			.python
			.string
			.assertEqualTo("\"Hello, world!\"")
	}

	@Test
	fun lines() {
		script(
			"x" lineTo script(leo.literal(10)),
			"y" lineTo script(leo.literal(20))
		)
			.python
			.string
			.assertEqualTo("(lambda v0: (lambda v1: (lambda v2: v2(v0)(v1))))(10)(20)")
	}

	@Test
	fun numberPlusNumber() {
		script(
			line(leo.literal(10)),
			plusName lineTo script(leo.literal(20))
		)
			.python
			.string
			.assertEqualTo("(lambda v0: (lambda x: lambda y: x + y)(v0((lambda v1: (lambda v2: v1))))(v0((lambda v1: (lambda v2: v2)))))((lambda v0: (lambda v1: (lambda v2: v2(v0)(v1))))(10)(20))")
	}
}