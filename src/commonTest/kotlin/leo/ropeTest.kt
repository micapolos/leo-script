package leo

import leo.base.assertEqualTo
import leo.base.orIfNull
import kotlin.test.Test

class RopeTest {
	@Test
	fun mapRope() {
		// Map summing previous, current and next.
		stack(1, 2, 3)
			.mapRope {
				0
					.plus(it.previousOrNull?.current.orIfNull { 0 })
					.plus(it.current)
					.plus(it.nextOrNull?.current.orIfNull { 0 })
			}
			.assertEqualTo(stack(3, 6, 5))
	}
}