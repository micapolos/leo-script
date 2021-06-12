package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ValueFunctionTest {
	@Test
	fun apply() {
		dictionary().function(body(script(contentName)))
			.applyEvaluation(value("foo")).get
			.assertEqualTo(value("foo"))
	}
}