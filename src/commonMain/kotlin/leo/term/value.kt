package leo.term

sealed class Value<out T> { override fun toString() = scriptLine.toString() }
data class NativeValue<T>(val native: T): Value<T>() { override fun toString() = super.toString() }
data class FunctionValue<T>(val function: Function<T>): Value<T>() { override fun toString() = super.toString() }

data class Function<out T>(val scope: Scope<T>, val term: Term<T>) { override fun toString() = scriptLine.toString() }

@Suppress("UNCHECKED_CAST")
fun <T> Value<T>.invokeEvaluation(value: Value<T>): Evaluation<T, Value<T>> =
	when (this) {
		is FunctionValue -> function.scope.plus(value).valueEvaluation(function.term)
		is NativeValue -> null!!
	}

fun <T> value(native: T): Value<T> = NativeValue(native)
fun <T> value(function: Function<T>): Value<T> = FunctionValue(function)
fun <T> function(scope: Scope<T>, term: Term<T>) = Function(scope, term)

val <T> Value<T>.native: T get() = (this as NativeValue).native

val <T> T.value: Value<T> get() = NativeValue(this)
