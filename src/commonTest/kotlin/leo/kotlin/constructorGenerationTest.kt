package leo.kotlin

import leo.base.assertEqualTo
import leo.fieldTo
import leo.lineTo
import leo.numberTypeLine
import leo.type
import kotlin.test.Test

class ConstructorGenerationTest {
	@Test
	fun prefix() {
		"point".fieldTo(type())
			.constructorString
			.assertEqualTo("fun point() = Point")

		"point"
			.fieldTo(
				type(
					"x" lineTo type(numberTypeLine),
					"y" lineTo type(numberTypeLine)))
			.constructorString
			.assertEqualTo("fun point(val x: DoubleX, val y: DoubleY) = Point(x, y)")
	}
}