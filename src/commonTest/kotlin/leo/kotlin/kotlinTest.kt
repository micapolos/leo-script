package leo.kotlin

import leo.atom
import leo.base.assertEqualTo
import leo.base.lines
import leo.choice
import leo.doing
import leo.fieldTo
import leo.isTypeLine
import leo.line
import leo.lineTo
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
					"@kotlin.jvm.JvmInline value class DoubleId(val number: Double)",
					"@kotlin.jvm.JvmInline value class StringId(val text: String)"))
	}

	@Test
	fun duplicateTypes() {
		type(
			"id" lineTo type(numberTypeLine),
			"id" lineTo type(numberTypeLine))
			.kotlin.string
			.assertEqualTo("@kotlin.jvm.JvmInline value class DoubleId(val number: Double)")
	}

	@Test
	fun structure() {
		type(
			"point" lineTo type(
				"x" lineTo type(numberTypeLine),
				"y" lineTo type(numberTypeLine)),
			"map" lineTo type(line(atom(
				typeStructure(
					"x" lineTo type(numberTypeLine),
					"y" lineTo type(numberTypeLine)) doing line(atom("length" fieldTo type(numberTypeLine)))))))
			.kotlin.string
			.assertEqualTo(
				lines(
					"@kotlin.jvm.JvmInline value class DoubleX(val number: Double)",
					"@kotlin.jvm.JvmInline value class DoubleY(val number: Double)",
					"data class Point(val x: DoubleX, val y: DoubleY)",
					"@kotlin.jvm.JvmInline value class DoubleLength(val number: Double)",
					"@kotlin.jvm.JvmInline value class Map(val doing: (DoubleX, DoubleY) -> DoubleLength)"))

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

	@Test
	fun isType() {
		type(isTypeLine)
			.kotlin.string
			.assertEqualTo("") // No code should be generated, as it's built-in.
	}
}