package leo.term

sealed class Native
data class StringNative(val string: String): Native()
object StringPlusStringNative: Native()

val String.native: Native get() = StringNative(this)
val Native.string: String get() = (this as StringNative).string

fun Scope<Native>.value(native: Native): Value<Native> =
	when (native) {
		is StringNative -> native.value
		StringPlusStringNative -> value(variable(1)).native.string.plus(value(variable(0)).native.string).native.value
	}

val nativeEvaluator: Evaluator<Native> get() = Evaluator { value(it) }