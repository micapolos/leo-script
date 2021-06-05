package leo

import leo.base.assertEqualTo
import leo.base.assertNull
import leo.base.indexed
import kotlin.test.Test

class TypeIndexingTest {
	@Test
	fun indexedLineOrNull() {
		typeStructure(
			"x" lineTo type(),
			textTypeLine,
			numberTypeLine,
			type(textTypeLine) doingLineTo type(numberTypeLine)
		).run {
			indexedLineOrNull("x").assertEqualTo(0 indexed ("x" lineTo type()))
			indexedLineOrNull(textName).assertEqualTo(1 indexed textTypeLine)
			indexedLineOrNull(numberName).assertEqualTo(2 indexed numberTypeLine)
			indexedLineOrNull(doingName).assertEqualTo(3 indexed (type(textTypeLine) doingLineTo type(numberTypeLine)))
			indexedLineOrNull("foo").assertNull
		}
	}
}