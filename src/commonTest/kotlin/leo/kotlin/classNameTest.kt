package leo.kotlin

import leo.base.assertEqualTo
import leo.doingLineTo
import leo.isTypeLine
import leo.lineTo
import leo.numberTypeLine
import leo.textTypeLine
import leo.type
import leo.typeStructure
import kotlin.test.Test

class ClassNameTest {
	@Test
	fun object_() {
		"foo"
			.lineTo(type())
			.className
			.assertEqualTo("Foo")
	}

	@Test
	fun valueClass() {
		"foo"
			.lineTo(type("bar" lineTo type()))
			.className
			.assertEqualTo("BarFoo")
	}

	@Test
	fun dataClass() {
		"foo"
			.lineTo(type("x" lineTo type(), "y" lineTo type()))
			.className
			.assertEqualTo("Foo")
	}

	@Test
	fun doingLine() {
		typeStructure(numberTypeLine).doingLineTo(textTypeLine)
			.className
			.assertEqualTo("Doing")
	}

	@Test
	fun literalLine() {
		numberTypeLine
			.className
			.assertEqualTo("Number")

		textTypeLine
			.className
			.assertEqualTo("Text")
	}

	@Test
	fun isTypeLine() {
		isTypeLine
			.className
			.assertEqualTo("Boolean")
	}
}