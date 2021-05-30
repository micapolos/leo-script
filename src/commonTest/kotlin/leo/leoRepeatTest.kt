package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class RepeatTest {
	@Test
	fun valueBindRepeating() {
		value(field(literal(10000))).evaluation
			.valueBindRepeating { value ->
				val counter = value.numberOrThrow.double.toInt()
				if (counter == 0) value.evaluation
				else value(field(literal(counter.dec()))).repeat.evaluation
			}
			.get
			.assertEqualTo(value(field(literal(0))))
	}
}
