package leo.base

import kotlin.test.Test

class AnyTest {
	@Test
	fun whileNotNull() {
		1
			.whileNotNull { notNullIf(this < 1000) { this * 2 } }
			.assertEqualTo(1024)
	}
}