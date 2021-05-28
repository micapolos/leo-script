package leo13

import leo.base.assertEqualTo
fun <V> Stack<V>.assertContains(vararg values: V) =
	assertEqualTo(stack(*values))