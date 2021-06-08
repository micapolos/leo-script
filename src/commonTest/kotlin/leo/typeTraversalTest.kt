package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeTraversalTest {
	@Test
	fun atom() {
		type(
			line(
				recursive(
					"line" lineTo type(
						"data" lineTo type("foo"),
						line(typeRecurse)))))
			.onlyLineOrNull
			?.atom
			.assertEqualTo(null)
	}
}