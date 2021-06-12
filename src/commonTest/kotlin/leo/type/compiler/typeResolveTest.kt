package leo.type.compiler

import leo.base.assertEqualTo
import leo.lineTo
import leo.numberTypeLine
import leo.structure
import leo.type
import kotlin.test.Test

class TypeResolveTest {
	@Test
	fun resolveGetOrNull() {
		structure(
			"x" lineTo type(
				"point" lineTo type(
					"x" lineTo type(numberTypeLine))))
			.resolveGetOrNull
			.assertEqualTo(structure("x" lineTo type(numberTypeLine)))
	}
}