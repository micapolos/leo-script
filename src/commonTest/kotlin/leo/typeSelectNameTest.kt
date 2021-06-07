package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeNameTest {
	@Test
	fun recursive() {
		line(recursive(atom("foo" fieldTo type())))
			.name
			.assertEqualTo("foo")
	}
}