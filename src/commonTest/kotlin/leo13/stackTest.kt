package leo13

import leo25.base.assertEqualTo
import kotlin.test.Test

class StackTest {
	@Test
	fun zip() {
		leo13.zip(stack(2, 1, 0), stack("two", "one", "zero"))
			.assertEqualTo(stack(2 to "two", 1 to "one", 0 to "zero"))
		leo13.zip(stack(2, 1, 0), stack("one", "zero"))
			.assertEqualTo(stack(2 to null, 1 to "one", 0 to "zero"))
		leo13.zip(stack(1, 0), stack("two", "one", "zero"))
			.assertEqualTo(stack(null to "two", 1 to "one", 0 to "zero"))
	}

	@Test
	fun map() {
		stack(1, 2, 3).map { inc() }.assertEqualTo(stack(2, 3, 4))
	}

	@Test
	fun flatMap() {
		stack(stack(1, 2), stack(3, 4))
			.flatMap { push(0) }
			.assertEqualTo(stack(1, 2, 0, 3, 4, 0))
	}

	@Test
	fun deduplicate() {
		stack<Int>().deduplicate.assertEqualTo(stack())
		stack(1, 2, 3, 2).deduplicate.assertEqualTo(stack(1, 3, 2))
	}

	@Test
	fun containsDistinct() {
		stack<Int>().containsDistinct.assertEqualTo(true)
		stack(1, 2).containsDistinct.assertEqualTo(true)
		stack(1, 2, 1).containsDistinct.assertEqualTo(false)
	}

}