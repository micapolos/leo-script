package leo.term

sealed class Native
data class StringNative(val string: String): Native()
data class StringPlusStringNative(val stringPlusString: StringPlusString): Native()

object StringPlusString

val stringPlusString get() = StringPlusString
val String.native: Native get() = StringNative(this)
val StringPlusString.native: Native get() = StringPlusStringNative(this)
val Native.string: String get() = (this as StringNative).string

fun Scope<Native>.value(native: Native): Value<Native> =
	when (native) {
		is StringNative -> value(native.string)
		is StringPlusStringNative -> value(native.stringPlusString)
	}

@Suppress("unused")
fun Scope<Native>.value(string: String): Value<Native> =
	string.native.value

fun Scope<Native>.value(@Suppress("UNUSED_PARAMETER") stringPlusString: StringPlusString): Value<Native> =
	value(1.variable).native.string.plus(value(0.variable).native.string).native.value

val nativeEvaluator: Evaluator<Native> get() = Evaluator { value(it) }

