package leo.expression.compiler

import leo.base.assertEqualTo
import leo.expression.resolveMake
import leo.expression.structure
import leo.lineTo
import leo.script
import kotlin.test.Test

class ScriptTest {
	@Test
	fun empty() {
		script().structure.assertEqualTo(structure())
	}

	@Test
	fun name() {
		script("hello").structure.assertEqualTo(structure().resolveMake("hello"))
	}

	@Test
	fun make() {
		script(
			"red" lineTo script(),
			"color" lineTo script())
			.structure
			.assertEqualTo(structure().resolveMake("red").resolveMake("color"))
	}
}