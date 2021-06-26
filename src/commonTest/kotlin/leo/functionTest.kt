package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ValueFunctionTest {
	@Test
	fun apply() {
		dictionary().function(body(script("bar")))
			.applyEvaluation(value(contentName)).get
			.assertEqualTo(value("bar"))
	}
}