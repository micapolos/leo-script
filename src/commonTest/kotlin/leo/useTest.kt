package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class UseTest {
	@Test
	fun scriptUseOrNull() {
		script("lib" lineTo script("open" lineTo script("gl")))
			.useOrNull
			.assertEqualTo(use("lib", "open", "gl"))
	}

	@Test
	fun fileString() {
		use("test", "all").fileString.assertEqualTo("test/all.leo")
	}
}