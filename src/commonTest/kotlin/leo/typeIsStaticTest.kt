package leo

import leo.base.assertFalse
import leo.base.assertTrue
import kotlin.test.Test

class TypeIsStaticTest {
	@Test
	fun static() {
		type().isStatic.assertTrue
		type("foo" lineTo type()).isStatic.assertTrue
		type(
			"point" lineTo type(
				"x" lineTo type("zero" lineTo type()),
				"y" lineTo type("zero" lineTo type())
			)
		).isStatic.assertTrue
	}

	@Test
	fun nonStatic() {
		type(textTypeLine).isStatic.assertFalse
		type(numberTypeLine).isStatic.assertFalse
		type(type() doingLineTo numberTypeLine).isStatic.assertFalse
		type(type(numberTypeLine) doingLineTo textTypeLine).isStatic.assertFalse
		type("point" lineTo type(
			"x" lineTo type(numberTypeLine),
			"y" lineTo type(numberTypeLine))).isStatic.assertFalse
		type(choice()).isStatic.assertFalse
	}
}