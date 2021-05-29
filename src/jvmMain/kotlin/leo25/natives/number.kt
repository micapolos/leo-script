package leo25.natives

import leo25.Number
import leo25.compareTo
import leo25.minus
import leo25.plus
import leo25.times

fun plus(a: Number, b: Number) = a.plus(b)
fun minus(a: Number, b: Number) = a.minus(b)
fun times(a: Number, b: Number) = a.times(b)
fun lessThan(a: Number, b: Number) = a < b
fun greaterThan(a: Number, b: Number) = a > b
fun lessOrEquals(a: Number, b: Number) = a <= b
fun greaterOrEquals(a: Number, b: Number) = a >= b
