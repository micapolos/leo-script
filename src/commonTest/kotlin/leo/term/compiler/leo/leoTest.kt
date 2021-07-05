package leo.term.compiler.leo

import leo.base.assertEqualTo
import leo.base.lines
import leo.line
import leo.lineTo
import leo.literal
import leo.script
import leo.string
import org.junit.Test

class LeoTest {
	@Test
	fun literal() {
		script(literal("Hello, world!"))
			.termScript
			.string
			.assertEqualTo("\"Hello, world!\"\n")
	}

	@Test
	fun lines() {
		script(
			"x" lineTo script(literal(10)),
			"y" lineTo script(literal(20))
		)
			.termScript
			.string
			.assertEqualTo(
				lines(
					"lambda lambda lambda",
					"  variable 2",
					"  apply variable 0",
					"  apply variable 1",
					"apply 10",
					"apply 20",
					""))
	}

	@Test
	fun numberAddNumber() {
		script(
			line(literal(10)),
			"add" lineTo script(literal(20))
		)
			.termScript
			.string
			.assertEqualTo(
				lines(
					"lambda",
					"  lambda lambda",
					"    variable 1",
					"    add variable 0",
					"  apply",
					"    variable 0",
					"    apply lambda lambda variable 1",
					"  apply",
					"    variable 0",
					"    apply lambda lambda variable 2",
					"apply",
					"  lambda lambda lambda",
					"    variable 2",
					"    apply variable 0",
					"    apply variable 1",
					"  apply 10",
					"  apply 20",
					""))
	}
}