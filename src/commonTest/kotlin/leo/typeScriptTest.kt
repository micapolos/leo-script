package leo

import leo.base.assertEqualTo
import leo.type.compiler.numberTypeScriptLine
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
						"x" lineTo script(numberTypeScriptLine),
						"y" lineTo script(numberTypeScriptLine))))
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

	@Test
	fun recursive() {
		type(
			line(recursive(
				"stack" lineTo type(choice(
					"empty" lineTo type(),
					"link" lineTo type(
						recurseTypeLine,
						numberTypeLine))))))
			.script
			.assertEqualTo(
				script(
					recursiveName lineTo script(
						"stack" lineTo script(
							choiceName lineTo script(
								"empty" lineTo script(),
								"link" lineTo script(
									recurseName lineTo script(),
									numberTypeScriptLine))))))
	}
}