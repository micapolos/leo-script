package leo

import leo.base.assertEqualTo
import leo.base.it
import kotlin.test.Test

class TypeRecursionTest {
	@Test
	fun atomRecursion_nonRecursive() {
		it("foo" lineTo type("bar"))
			.atomRecursion
			.apply
			.assertEqualTo(atom("foo" fieldTo type("bar")))
	}

	@Test
	fun atomRecursion_recursive() {
		recursiveLine("foo" lineTo type("bar" lineTo type(recurseTypeLine)))
			.atomRecursion
			.apply
			.assertEqualTo(atom(
				"foo" fieldTo type(
					recursiveLine("bar" lineTo type(
						"foo" lineTo type(recurseTypeLine))))))
	}
}