package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class FunctionTest {
	@Test
	fun apply() {
		dictionary().function(body(script("name")))
			.applyEvaluation(value("name" fieldTo value("foo"))).get
			.assertEqualTo(value("name" fieldTo value("foo")))
	}
}