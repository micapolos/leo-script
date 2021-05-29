package leo25

import leo.base.assertEqualTo
import leo14.literal
import kotlin.test.Test

class RepeatTest {
	@Test
	fun valueBindRepeating() {
		value(field(literal(10000))).leo
			.valueBindRepeating { value ->
				val counter = value.numberOrThrow.double.toInt()
				if (counter == 0) value.leo
				else value(field(literal(counter.dec()))).repeat.leo
			}
			.get
			.assertEqualTo(value(field(literal(0))))
	}
}
