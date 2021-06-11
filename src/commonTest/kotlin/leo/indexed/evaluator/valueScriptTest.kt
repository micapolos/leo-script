package leo.indexed.evaluator

import leo.base.assertEqualTo
import leo.base.indexed
import leo.choice
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class ValueScriptTest {
	@Test
	fun choices() {
		type("id" lineTo type(choice(textTypeLine, numberTypeLine)))
			.script(0.indexed("foo"))
			.assertEqualTo(script("id" lineTo script(literal("foo"))))
	}
}