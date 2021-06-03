package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class FunctionTest {
	@Test
	fun apply() {
		dictionary().function(body(script("ok")))
			.applyEvaluation(value("foo")).get
			.assertEqualTo(value("ok" fieldTo value("foo")))
	}
}