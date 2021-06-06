package leo.kotlin

import leo.atom
import leo.base.assertEqualTo
import leo.base.lines
import leo.choice
import leo.doingLineTo
import leo.fieldTo
import leo.line
import leo.lineTo
import leo.list
import leo.numberTypeLine
import leo.textTypeLine
import leo.type
import leo.typeStructure
import kotlin.test.Test

class ConstructorGenerationTest {
	@Test
	fun structure() {
		"point".fieldTo(type())
			.constructorString
			.assertEqualTo("fun point() = Point")

		"point"
			.fieldTo(
				type(
					"x" lineTo type(numberTypeLine),
					"y" lineTo type(numberTypeLine)))
			.constructorString
			.assertEqualTo("inline fun point(x: DoubleX, y: DoubleY) = Point(x, y)")
	}

	@Test
	fun choice_fieldCases() {
		"id".fieldTo(type(choice()))
			.constructorString
			.assertEqualTo("")

		"id"
			.fieldTo(type(choice(numberTypeLine, textTypeLine)))
			.constructorString
			.assertEqualTo(
				lines(
					"inline fun id(number: Double): Id = NumberId(number)",
					"inline fun id(text: String): Id = TextId(text)"
				)
			)
	}

	@Test
	fun choice_doingCase() {
		"id"
			.fieldTo(type(choice(
				typeStructure(numberTypeLine) doingLineTo textTypeLine)))
			.constructorString
			.assertEqualTo(
				lines("inline fun id(doing: (Double) -> String): Id = DoingId(doing)"))
	}

	@Test
	fun choice_listCase() {
		"id"
			.fieldTo(type(choice(line(atom(list(numberTypeLine))))))
			.constructorString
			.assertEqualTo(
				lines("inline fun id(list: Stack<Double>): Id = ListId(list)"))
	}
}