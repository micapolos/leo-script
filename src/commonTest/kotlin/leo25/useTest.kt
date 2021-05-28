package leo25

import leo.base.assertEqualTo
import leo14.lineTo
import leo14.script
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