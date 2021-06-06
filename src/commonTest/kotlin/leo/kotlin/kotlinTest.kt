package leo.kotlin

import leo.atom
import leo.base.assertEqualTo
import leo.base.lines
import leo.choice
import leo.doing
import leo.fieldTo
import leo.line
import leo.lineTo
import leo.list
import leo.numberTypeLine
import leo.textTypeLine
import leo.type
import leo.typeStructure
import kotlin.test.Test

class KotlinTest {
	@Test
	fun objectTypes() {
		type(
			"Yes" lineTo type(),
			"No" lineTo type())
			.kotlin.string
			.assertEqualTo(
				lines(
					"object Yes",
					"object No"))
	}

	@Test
	fun conflictingTypes() {
		type(
			"id" lineTo type(numberTypeLine),
			"id" lineTo type(textTypeLine))
			.kotlin.string
			.assertEqualTo(
				lines(
					"data class DoubleId(val number: Double)",
					"data class StringId(val text: String)"))
	}

	@Test
	fun duplicateTypes() {
		type(
			"id" lineTo type(numberTypeLine),
			"id" lineTo type(numberTypeLine))
			.kotlin.string
			.assertEqualTo("data class DoubleId(val number: Double)")
	}

	@Test
	fun structure() {
		type(
			line(atom(list(
				"point" lineTo type(
					"x" lineTo type(numberTypeLine),
					"y" lineTo type(numberTypeLine))))),
			"map" lineTo type(line(atom(
				typeStructure(
					"x" lineTo type(numberTypeLine),
					"y" lineTo type(numberTypeLine)) doing line(atom("length" fieldTo type(numberTypeLine)))))))
			.kotlin.string
			.assertEqualTo(
				lines(
					"data class DoubleX(val number: Double)",
					"data class DoubleY(val number: Double)",
					"data class Point(val x: DoubleX, val y: DoubleY)",
					"data class DoubleLength(val number: Double)",
					"data class Map(val doing: (DoubleX, DoubleY) -> DoubleLeŻngth)"))

	}

	@Test
	fun choices() {
		type(
			"bool" lineTo type(
				choice(
					"yes" lineTo type(),
					"no" lineTo type())))
			.kotlin.string
			.assertEqualTo(
				lines(
					"object Yes",
					"object No",
					"sealed class Bool",
					"data class YesBool(val yes: Yes): Bool()",
					"data class NoBool(val no: No): Bool()"))

	}
}