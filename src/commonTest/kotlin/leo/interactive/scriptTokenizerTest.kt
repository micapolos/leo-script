package leo.interactive

import leo.base.assertEqualTo
import leo.line
import leo.lineTo
import leo.literal
import leo.script
import kotlin.test.Test

class ScriptTokenizerTest {
	@Test
	fun name() {
		scriptTokenizer
			.process(token(begin("zero")))
			.process(token(end))
			.state
			.assertEqualTo(script("zero"))
	}

	@Test
	fun number() {
		scriptTokenizer
			.process(token(literal(10)))
			.state
			.assertEqualTo(script(line(literal(10))))
	}

	@Test
	fun text() {
		scriptTokenizer
			.process(token(literal("foo")))
			.state
			.assertEqualTo(script(line(literal("foo"))))
	}

	@Test
	fun simpleField() {
		scriptTokenizer
			.process(token(begin("color")))
			.process(token(begin("red")))
			.process(token(end))
			.process(token(end))
			.state
			.assertEqualTo(script("color" lineTo script("red")))
	}

	@Test
	fun complexField() {
		scriptTokenizer
			.process(token(begin("point")))
			.process(token(begin("x")))
			.process(token(end))
			.process(token(begin("y")))
			.process(token(end))
			.process(token(end))
			.state
			.assertEqualTo(script("point" lineTo script("x", "y")))
	}

	@Test
	fun structure() {
		scriptTokenizer
			.process(token(begin("point")))
			.process(token(begin("x")))
			.process(token(literal(10)))
			.process(token(end))
			.process(token(begin("y")))
			.process(token(literal(20)))
			.process(token(end))
			.process(token(end))
			.state
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script(literal(10)),
						"y" lineTo script(literal(20)))))
	}
}