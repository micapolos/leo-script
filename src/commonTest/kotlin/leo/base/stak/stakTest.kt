package leo.base.stak

import leo.base.assertEqualTo
import leo.base.assertNull
import leo.base.iterate
import leo.base.nullOf
import kotlin.test.Test

val s0 = node("zero", null)
val s1 = node("one", link(s0, null))
val s2 = node("two", link(s1, link(s0, null)))
val s3 = node("three", link(s2, null))
val s4 = node("four", link(s3, link(s2, link(s0, null))))
val s5 = node("five", link(s4, null))
val s6 = node("six", link(s5, link(s4, null)))
val s7 = node("seven", link(s6, null))
val s8 = node("eight", link(s7, link(s6, link(s4, link(s0, null)))))
val s9 = node("nine", link(s8, null))
val s10 = node("ten", link(s9, link(s8, null)))

class StakTest {
	@Test
	fun staks() {
		val x = nullOf<Node<String>>()
		val x0 = x.push("zero")
		val x1 = x0.push("one")
		val x2 = x1.push("two")
		val x3 = x2.push("three")
		val x4 = x3.push("four")
		val x5 = x4.push("five")
		val x6 = x5.push("six")
		val x7 = x6.push("seven")
		val x8 = x7.push("eight")
		val x9 = x8.push("nine")
		val x10 = x9.push("ten")

		x0.assertEqualTo(s0)
		x1.assertEqualTo(s1)
		x2.assertEqualTo(s2)
		x3.assertEqualTo(s3)
		x4.assertEqualTo(s4)
		x5.assertEqualTo(s5)
		x6.assertEqualTo(s6)
		x7.assertEqualTo(s7)
		x8.assertEqualTo(s8)
		x9.assertEqualTo(s9)
		x10.assertEqualTo(s10)

		x10.pop!!.assertEqualTo(x9)
		x10.pop!!.pop!!.assertEqualTo(x8)
		x10.pop!!.pop!!.pop!!.assertEqualTo(x7)
		x10.pop!!.pop!!.pop!!.pop!!.assertEqualTo(x6)
		x10.pop!!.pop!!.pop!!.pop!!.pop!!.assertEqualTo(x5)
		x10.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.assertEqualTo(x4)
		x10.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.assertEqualTo(x3)
		x10.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.assertEqualTo(x2)
		x10.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.assertEqualTo(x1)
		x10.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.assertEqualTo(x0)
		x10.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop!!.pop.assertNull

		x10.pop(0).assertEqualTo(x10)
		x10.pop(1).assertEqualTo(x9)
		x10.pop(2).assertEqualTo(x8)
		x10.pop(3).assertEqualTo(x7)
		x10.pop(4).assertEqualTo(x6)
		x10.pop(5).assertEqualTo(x5)
		x10.pop(6).assertEqualTo(x4)
		x10.pop(7).assertEqualTo(x3)
		x10.pop(8).assertEqualTo(x2)
		x10.pop(9).assertEqualTo(x1)
		x10.pop(10).assertEqualTo(x0)
		x10.pop(11).assertNull
	}

	@Test
	fun fold() {
		"".fold(stakOf(1, 2, 3)) { plus(it.toString()) }.assertEqualTo("321")
	}

	@Test
	fun large() {
		val size = 1000000
		val s = emptyStak<Int>().iterate(size) { push(123) }
		s.top(size - 1).assertEqualTo(123)
		s.top(size).assertNull
	}

	@Test
	fun size() {
		stakOf<Unit>().size.assertEqualTo(0)
		stakOf(10).size.assertEqualTo(1)
		stakOf(10, 20).size.assertEqualTo(2)
		stakOf(10, 20, 30).size.assertEqualTo(3)
		stakOf(10, 20, 30, 40).size.assertEqualTo(4)
	}

	@Test
	fun get() {
		stakOf(10, 20, 30, 40).run {
			get(0).assertEqualTo(10)
			get(1).assertEqualTo(20)
			get(2).assertEqualTo(30)
			get(3).assertEqualTo(40)
			get(4).assertNull
		}
	}

//	@Test
//	fun performance() {
//		val size = 1000000
//		val access = 100
//
//		repeat(5) {
//			println("======")
//
//			var stak0: Stak<Int>? = null
//			var stack0: Stack<Int>? = null
//			var list0: List<Int>? = null
//
//			print("Create Stack: ")
//			printTime {
//				stack0 = stack<Int>().iterate(size) { push(0) }
//			}
//
//			print("Create Stak: ")
//			printTime {
//				stak0 = emptyStak<Int>().iterate(size) { push(Random.nextInt()) }
//			}
//
//			print("Create List: ")
//			printTime {
//				list0 = mutableListOf<Int>().iterate(size) { also { add(Random.nextInt()) } }.toList()
//			}
//
//			var sum = 0
//			val stack = stack0!!
//			print("Random access Stack ($access): ")
//			printTime {
//				repeat(access) {
//					sum += stack.get(Random.nextInt(size))!!
//				}
//			}
//
//			val stak = stak0!!
//			print("Random access Stak ($access): ")
//			printTime {
//				repeat(access) {
//					sum += stak.top(Random.nextInt(size))!!
//				}
//			}
//
//			val list = list0!!
//			print("Random access List ($access): ")
//			printTime {
//				repeat(access) {
//					sum += list.get(Random.nextInt(size))
//				}
//			}
//
//			print("Random access Stak ($size): ")
//			printTime {
//				repeat(size) {
//					sum += stak.top(Random.nextInt(size))!!
//				}
//			}
//
//			print("Random access List ($size): ")
//			printTime {
//				repeat(size) {
//					sum += list.get(Random.nextInt(size))
//				}
//			}
//
//			println("Result: $sum")
//		}
//	}
}