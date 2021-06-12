package leo.named.value

import leo.base.assertEqualTo
import leo.base.assertFails
import leo.literal
import org.junit.Test

class ValueTest {
	@Test
	fun get() {
		val structure = structure<Unit>(
			"point" valueTo structure(
				"x" valueTo structure(value(literal(10))),
				"y" valueTo structure(value(literal(20)))))

		structure
			.get("x")
			.assertEqualTo(
				structure(
					"x" valueTo structure(value(literal(10)))))

		structure
			.get("y")
			.assertEqualTo(
				structure(
					"y" valueTo structure(value(literal(20)))))

		assertFails {
			structure.get("z")
		}
	}
}