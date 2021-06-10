package leo.indexed

import leo.base.assertEqualTo
import leo.base.assertNull
import leo.base.indexed
import leo.lineTo
import leo.numberTypeLine
import leo.textTypeLine
import leo.type
import leo.typeStructure
import kotlin.test.Test

class TypeIndexingTest {
	@Test
	fun indexedLine() {
		val structure = typeStructure(
			"x" lineTo type(numberTypeLine),
			"y" lineTo type(textTypeLine))

		structure
			.indexedLineOrNull("x")
			.assertEqualTo(0.indexed("x" lineTo type(numberTypeLine)))

		structure
			.indexedLineOrNull("y")
			.assertEqualTo(1.indexed("y" lineTo type(textTypeLine)))

		structure
			.indexedLineOrNull("z")
			.assertNull
	}

	@Test
	fun get() {
		val structure = type(
			"point" lineTo type(
				"x" lineTo type(numberTypeLine),
				"y" lineTo type(textTypeLine)))

		structure
			.getIndexedOrNull("x")
			.assertEqualTo(0.indexed(type("x" lineTo type(numberTypeLine))))

		structure
			.getIndexedOrNull("y")
			.assertEqualTo(1.indexed(type("y" lineTo type(textTypeLine))))

		structure
			.getIndexedOrNull("z")
			.assertNull
	}
}