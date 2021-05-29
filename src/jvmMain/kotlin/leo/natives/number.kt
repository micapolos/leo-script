package leo.natives

import leo.Number
import leo.compareTo
import leo.minus
import leo.plus
import leo.times

fun plus(a: Number, b: Number) = a.plus(b)
fun minus(a: Number, b: Number) = a.minus(b)
fun times(a: Number, b: Number) = a.times(b)
fun lessThan(a: Number, b: Number) = a < b
fun greaterThan(a: Number, b: Number) = a > b
fun lessOrEquals(a: Number, b: Number) = a <= b
fun greaterOrEquals(a: Number, b: Number) = a >= b
