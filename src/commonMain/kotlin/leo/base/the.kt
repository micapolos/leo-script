package leo.base

data class The<out V>(
	val value: V)

data class TwoOf<out First, out Second>(
	val first: First,
	val second: Second)

data class ThreeOf<out First, out Second, out Third>(
	val first: First,
	val second: Second,
	val third: Third)

data class FourOf<out First, out Second, out Third, out Fourth>(
	val first: First,
	val second: Second,
	val third: Third,
	val fourth: Fourth)

val <V> V.the
	get() =
		The(this)

fun <V> the(value: V) =
	value.the

fun <First, Second> these(first: First, second: Second) =
	TwoOf(first, second)

fun <First, Second, Third> these(first: First, second: Second, third: Third) =
	ThreeOf(first, second, third)

fun <First, Second, Third, Fourth> these(first: First, second: Second, third: Third, fourth: Fourth) =
	FourOf(first, second, third, fourth)