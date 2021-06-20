package leo.named.compiler

import leo.base.assertNotNull
import leo.line
import leo.lineTo
import leo.literal
import leo.named.library.preludeCompiler
import leo.plusName
import leo.script
import kotlin.test.Test

class PreludeCompileTest {
	@Test
	fun numberPlusNumber() {
		script(
			line(literal(10)),
			plusName lineTo script(literal(20)))
			.typedExpression(preludeCompiler)
			.assertNotNull
	}
}