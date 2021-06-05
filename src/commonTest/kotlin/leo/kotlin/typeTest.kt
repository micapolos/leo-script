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

class GenerationTest {
	@Test
	fun doingTypeName() {
		line(atom(typeStructure(numberTypeLine, textTypeLine) doing numberTypeLine))
			.typeName
			.assertEqualTo("(Double, String) -> Double")
	}

	@Test
	fun listTypeName() {
		line(atom(list(numberTypeLine)))
			.typeName
			.assertEqualTo("Stack<Double>")
	}

	@Test
	fun literalTypeName() {
		numberTypeLine.typeName.assertEqualTo("Double")
		textTypeLine.typeName.assertEqualTo("String")
	}

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
					"data class Id(val number: Double)",
					"data class Id2(val text: String)"))
	}

	@Test
	fun duplicateTypes() {
		type(
			"id" lineTo type(numberTypeLine),
			"id" lineTo type(numberTypeLine))
			.kotlin.string
			.assertEqualTo("data class Id(val number: Double)")
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
					"data class X(val number: Double)",
					"data class Y(val number: Double)",
					"data class Point(val x: X, val y: Y)",
					"data class Length(val number: Double)",
					"data class Map(val doing: (X, Y) -> Length)"))

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