package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ValueValueDoingTest {
	@Test
	fun apply() {
		dictionary().doing(body(script("bar")))
			.giveEvaluation(value(contentName)).get
			.assertEqualTo(value("bar"))
	}
}