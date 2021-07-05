package leo.term.typed

import leo.base.assertEqualTo
import leo.term.anyEvaluator
import leo.term.anyTerm
import leo.term.value
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class TypedTest {
	@Test
	fun plus() {
		typedTerm<Any>()
			.plus(typed("foo".anyTerm, textTypeLine))
			.plus(typed("bar".anyTerm, textTypeLine))
			.headOrNull!!
			.typedValue(anyEvaluator)
			.assertEqualTo(typed("bar".value, type(textTypeLine)))
	}
}