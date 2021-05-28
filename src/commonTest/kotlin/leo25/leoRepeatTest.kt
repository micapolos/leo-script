package leo25

import leo.base.assertEqualTo
import kotlin.test.Test

class RepeatTest {
	@Test
	fun noStackOverflow() {
		100000.leo
			.bindRepeating { counter ->
				if (counter == 0) counter.leo
				else counter.dec().leo.repeat
			}
			.get
			.assertEqualTo(0)
	}
}
