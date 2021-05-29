package leo25

import leo25.base.assertEqualTo
import leo13.assertContains
import kotlin.test.Test

class ScriptTest {
	@Test
	fun code() {
		script(
			line(literal(2)),
			line("plus" fieldTo literal(3)))
			.code
			.assertEqualTo("2.plus(3)")

		script(
			line(literal(2.5)),
			line("plus" fieldTo literal(3.5)))
			.code
			.assertEqualTo("2.5.plus(3.5)")

		script(
			"vec" fieldTo script(
				"x" fieldTo literal(1),
				"y" fieldTo literal(2),
				"name" fieldTo literal("my vector")))
			.code
			.assertEqualTo("vec(x(1).y(2).name(\"my vector\"))")
	}

	@Test
	fun nameStackOrNull() {
		script().nameStackOrNull!!.assertContains()
		script("x").nameStackOrNull!!.assertContains("x")
		script("x" lineTo script("y")).nameStackOrNull!!.assertContains("y", "x")
	}
}