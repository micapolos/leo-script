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
			.plusFn(token(begin("zero")))
			.plusFn(token(end))
			.state
			.assertEqualTo(script("zero"))
	}

	@Test
	fun number() {
		scriptTokenizer
			.plusFn(token(literal(10)))
			.state
			.assertEqualTo(script(line(literal(10))))
	}

	@Test
	fun text() {
		scriptTokenizer
			.plusFn(token(literal("foo")))
			.state
			.assertEqualTo(script(line(literal("foo"))))
	}

	@Test
	fun simpleField() {
		scriptTokenizer
			.plusFn(token(begin("color")))
			.plusFn(token(begin("red")))
			.plusFn(token(end))
			.plusFn(token(end))
			.state
			.assertEqualTo(script("color" lineTo script("red")))
	}

	@Test
	fun complexField() {
		scriptTokenizer
			.plusFn(token(begin("point")))
			.plusFn(token(begin("x")))
			.plusFn(token(end))
			.plusFn(token(begin("y")))
			.plusFn(token(end))
			.plusFn(token(end))
			.state
			.assertEqualTo(script("point" lineTo script("x", "y")))
	}

	@Test
	fun structure() {
		scriptTokenizer
			.plusFn(token(begin("point")))
			.plusFn(token(begin("x")))
			.plusFn(token(literal(10)))
			.plusFn(token(end))
			.plusFn(token(begin("y")))
			.plusFn(token(literal(20)))
			.plusFn(token(end))
			.plusFn(token(end))
			.state
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script(literal(10)),
						"y" lineTo script(literal(20)))))
	}
}