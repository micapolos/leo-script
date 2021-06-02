package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ScriptTypeTest {
	@Test
	fun empty() {
		script().type.assertEqualTo(type())
	}

	@Test
	fun any() {
		script(anyName).type.assertEqualTo(anyType())
	}

	@Test
	fun anyPlusAny() {
		script(
			anyName lineTo script(),
			"plus" lineTo script(anyName))
			.type
			.assertEqualTo(anyType("plus" fieldTo anyType))
	}
}