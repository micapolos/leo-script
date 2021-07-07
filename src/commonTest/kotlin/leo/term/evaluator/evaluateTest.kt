package leo.term.evaluator

import leo.base.assertEqualTo
import leo.choiceName
import leo.line
import leo.lineTo
import leo.literal
import leo.notName
import leo.numberTypeScriptLine
import leo.script
import leo.selectName
import leo.textTypeScriptLine
import leo.theName
import leo.typeName
import kotlin.test.Test

class EvaluateTest {
	@Test
	fun numberType() {
		script(
			line(literal(10)),
			typeName lineTo script())
			.evaluate
			.assertEqualTo(script(numberTypeScriptLine))
	}

	@Test
	fun textType() {
		script(
			line(literal("foo")),
			typeName lineTo script())
			.evaluate
			.assertEqualTo(script(textTypeScriptLine))
	}


	@Test
	fun selectType() {
		script(
			selectName lineTo script(
				theName lineTo script(literal(10)),
				notName lineTo script(textTypeScriptLine)),
			typeName lineTo script())
			.evaluate
			.assertEqualTo(
				script(
					choiceName lineTo script(
						numberTypeScriptLine,
						textTypeScriptLine)))
	}
}