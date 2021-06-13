package leo.named.value

import leo.base.assertEqualTo
import leo.base.assertFails
import org.junit.Test

class ValueTest {
	@Test
	fun get() {
		val structure = value(
			"point" lineTo value(
				"x" lineTo numberValue(10),
				"y" lineTo numberValue(20)))

		structure
			.get("x")
			.assertEqualTo(
				value(
					"x" lineTo numberValue(10)))

		structure
			.get("y")
			.assertEqualTo(
				value(
					"y" lineTo numberValue(20)))

		assertFails {
			structure.get("z")
		}
	}
}