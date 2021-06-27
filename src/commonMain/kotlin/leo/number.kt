package leo

import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

@kotlin.jvm.JvmInline
value class Number(val double: Double) {
	override fun toString() =
		if (double == double.roundToLong().toDouble()) double.toLong().toString()
		else double.toString()
}

fun number(double: Double): Number = Number(double)
fun number(int: Int): Number = number(int.toDouble())
fun number(long: Long): Number = number(long.toDouble())

val Int.number: Number get() = number(this)
val Double.number: Number get() = number(this)

val String.numberOrNull: Number?
	get() =
		toDoubleOrNull()?.let { number(it) }

val Number.code
	get() =
		toString()

operator fun Number.plus(number: Number) =
	number(double + number.double)

operator fun Number.minus(number: Number) =
	number(double - number.double)

operator fun Number.times(number: Number) =
	number(double * number.double)

operator fun Number.div(number: Number) =
	number(double / number.double)

operator fun Number.compareTo(number: Number) =
	double.compareTo(number.double)

fun Number.isEqualTo(number: Number) =
	equals(number)

operator fun Number.unaryMinus() =
	number(-double)

val Number.sinus: Number
	get() =
		sin(double).number

val Number.cosinus: Number
	get() =
		cos(double).number

val Number.string: String
	get() =
		toString()