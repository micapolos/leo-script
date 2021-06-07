package leo.kotlin

import leo.atom
import leo.base.assertEqualTo
import leo.doing
import leo.isTypeLine
import leo.line
import leo.list
import leo.numberTypeLine
import leo.textTypeLine
import leo.typeStructure
import kotlin.test.Test

class TypeNameTest {
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
	fun isTypeName() {
		isTypeLine.typeName.assertEqualTo("Boolean")
	}
}