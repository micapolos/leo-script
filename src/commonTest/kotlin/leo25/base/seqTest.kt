package leo25.base

import kotlin.test.Test

class BlockTest {
	@Test
	fun content() {
		seq<Int>().assertContains()
		seq(1).assertContains(1)
		seq(1, 2, 3).assertContains(1, 2, 3)
	}

	@Test
	fun iterator() {
		seq<Int>().assertContains()
		seq(1).assertContains(1)
		seq(1, 2, 3).assertContains(1, 2, 3)
	}

	@Test
	fun map() {
		seq(1, 2, 3).map { toString() }.assertContains("1", "2", "3")
	}

	@Test
	fun flatten() {
		seq<Seq<Int>>().flat.assertContains()
		seq(seq<Int>()).flat.assertContains()
		seq(seq<Int>(), seq()).flat.assertContains()
		seq(seq(1), seq(2)).flat.assertContains(1, 2)
		seq(seq(1, 2), seq(3, 4)).flat.assertContains(1, 2, 3, 4)
		seq(seq(), seq(1, 2), seq(), seq(3, 4), seq()).flat.assertContains(1, 2, 3, 4)
	}

	@Test
	fun intercept() {
		seq<String>().intercept(",").assertContains()
		seq("one").intercept(",").assertContains("one")
		seq("one", "two").intercept(",").assertContains("one", ",", "two")
		seq("one", "two", "three").intercept(",").assertContains("one", ",", "two", ",", "three")
	}

	@Test
	fun zip() {
		zip(seq(), seq()).assertContains()
		zip(seq(1, 2), seq()).assertContains(1 to null, 2 to null)
		zip(seq(), seq("a", "b")).assertContains(null to "a", null to "b")
		zip(seq(1), seq("a", "b")).assertContains(1 to "a", null to "b")
		zip(seq(1, 2), seq("a")).assertContains(1 to "a", 2 to null)
		zip(seq(1, 2), seq("a", "b")).assertContains(1 to "a", 2 to "b")
	}

	@Test
	fun takeOrNull() {
		seq(1, 2, 3).takeOrNull(0).assertContains()
		seq(1, 2, 3).takeOrNull(2).assertContains(1, 2)
		seq(1, 2, 3).takeOrNull(5).assertContains(1, 2, 3, null, null)
	}
}

fun <T> Seq<T>.assertContains(vararg items: T) {
	val list = ArrayList<T>()
	var sequence = this
	while (true) {
		val nonEmptySequence = sequence.nodeOrNullFn() ?: break
		list.add(nonEmptySequence.first)
		sequence = nonEmptySequence.remaining
	}
	list.assertEqualTo(listOf(*items))
}
