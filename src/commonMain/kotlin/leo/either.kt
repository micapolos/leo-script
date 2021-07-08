package leo

import kotlin.reflect.KClass

sealed class Or<out First, out Second>

data class FirstOr<First, Second>(val first: First) : Or<First, Second>()
data class SecondOr<First, Second>(val second: Second) : Or<First, Second>()

infix fun <First, Second : Any> First.or(@Suppress("UNUSED_PARAMETER") second: KClass<Second>): Or<First, Second> =
  FirstOr(this)

infix fun <First : Any, Second> KClass<First>.or(second: Second): Or<First, Second> = SecondOr(second)
