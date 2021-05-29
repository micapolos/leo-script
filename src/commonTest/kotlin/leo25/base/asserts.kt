package leo25.base

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

fun <V> V.assertReturns() {}

fun <V> V.assertEqualTo(other: V, message: String? = null) =
	assertEquals(other, this, message)

infix fun <V> V.assertEqualTo(other: V) =
	assertEqualTo(other, null)

infix fun <V> V.assertNotEqualTo(other: V) =
	assertNotEquals(other, this)

val Boolean.assert
	get() =
		assertEqualTo(true)

val Boolean.assertFalse
	get() =
		assertEqualTo(false)

val Boolean.assertTrue
	get() =
		assertEqualTo(true)

val <V : Any> V?.assertNotNull
	get() =
		kotlin.test.assertNotNull(this)

val <V : Any> V?.assertNull
	get() =
		kotlin.test.assertNull(this)

fun <V, R> V.assertFails(fn: V.() -> R) =
	kotlin.test.assertFails { fn() }
