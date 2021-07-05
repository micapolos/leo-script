package leo.term.typed

import leo.base.assertEqualTo
import leo.literal
import leo.term.anyEvaluator
import leo.term.value
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class TypedTest {
	@Test
	fun plus() {
		typedTerm(
			typedLine(literal("foo")),
			typedLine(literal("bar")))
			.headOrNull!!
			.typedValue(anyEvaluator)
			.assertEqualTo(typed("bar".value, type(textTypeLine)))
	}

	@Test
	fun content() {
		typedTerm(
			"point" lineTo typedTerm(
				"x" lineTo typedTerm(typedLine(literal(10))),
				"y" lineTo typedTerm(typedLine(literal(20)))))
			.contentOrNull
			.assertEqualTo(
				typedTerm(
					"x" lineTo typedTerm(typedLine(literal(10))),
					"y" lineTo typedTerm(typedLine(literal(20)))))
	}
}