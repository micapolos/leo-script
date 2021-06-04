package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeScriptTest {
	@Test
	fun structure() {
		type(
			"point" lineTo type(
				"x" lineTo type(numberTypeLine),
				"y" lineTo type(numberTypeLine)))
			.script
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script("number"),
						"y" lineTo script("number"))))
	}

	@Test
	fun choice() {
		type(
			isName lineTo type(choice(
				yesName lineTo type(),
				noName lineTo type())))
			.script
			.assertEqualTo(
				script(
					isName lineTo script(
						choiceName lineTo script(
							yesName lineTo script(),
							noName lineTo script()))))
	}
}