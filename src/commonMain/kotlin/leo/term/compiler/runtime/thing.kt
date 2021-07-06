package leo.term.compiler.runtime

sealed class Thing
data class DoubleThing(val double: Double): Thing()
data class StringThing(val string: String): Thing()
object DoubleAddDoubleThing: Thing()
object DoubleSubtractDoubleThing: Thing()
object DoubleMultiplyByDoubleThing: Thing()
object StringAppendStringThing: Thing()

val Double.thing: Thing get() = DoubleThing(this)
val String.thing: Thing get() = StringThing(this)

val Thing.double: Double get() = (this as DoubleThing).double
val Thing.string: String get() = (this as StringThing).string
