package leo25.base

import kotlin.test.Test

class AppendableTest {
	@Test
	fun tryAppend() {
		appendableString {
			it.tryAppend {
				append("foo").let { null }
			} ?: it.append("bar")
		}.assertEqualTo("bar")

		appendableString {
			it.tryAppend {
				append("foo")
			} ?: it.append("bar")
		}.assertEqualTo("foo")
	}
}