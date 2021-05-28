package leo25.natives

import leo14.Number
import leo14.compareTo
import leo14.minus
import leo14.plus
import leo14.times

fun plus(a: Number, b: Number) = a.plus(b)
fun minus(a: Number, b: Number) = a.minus(b)
fun times(a: Number, b: Number) = a.times(b)
fun lessThan(a: Number, b: Number) = a < b
fun greaterThan(a: Number, b: Number) = a > b
fun lessOrEquals(a: Number, b: Number) = a <= b
fun greaterOrEquals(a: Number, b: Number) = a >= b
