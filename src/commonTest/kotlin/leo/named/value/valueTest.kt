package leo.named.value

import leo.base.assertEqualTo
import leo.base.assertFails
import leo.literal
import org.junit.Test

class ValueTest {
	@Test
	fun get() {
		val structure = value<Unit>(
			"point" lineTo value(
				"x" lineTo value(valueLine(literal(10))),
				"y" lineTo value(valueLine(literal(20)))))

		structure
			.get("x")
			.assertEqualTo(
				value(
					"x" lineTo value(valueLine(literal(10)))))

		structure
			.get("y")
			.assertEqualTo(
				value(
					"y" lineTo value(valueLine(literal(20)))))

		assertFails {
			structure.get("z")
		}
	}
}