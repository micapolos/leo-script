package leo25

import leo25.base.assertEqualTo
fun <V> Stack<V>.assertContains(vararg values: V) =
	assertEqualTo(stack(*values))