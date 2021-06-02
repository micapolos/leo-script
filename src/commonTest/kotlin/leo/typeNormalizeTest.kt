package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeNormalizeTest {
	@Test
	fun normalize() {
		anyType(
			"foo" fieldTo type(),
			"bar" fieldTo type())
			.normalize
			.assertEqualTo(type("bar" fieldTo type("foo" fieldTo anyType)))
	}
}