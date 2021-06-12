package leo.named.value

import leo.base.assertEqualTo
import leo.literal
import org.junit.Test

class ValueTest {
	@Test
	fun get() {
		structure<Unit>(
			"point" valueTo structure(
				"x" valueTo structure(value(literal(10))),
				"y" valueTo structure(value(literal(10)))))
			.get("x")
			.assertEqualTo(
				structure(
					"x" valueTo structure(value(literal(10)))))
	}
}