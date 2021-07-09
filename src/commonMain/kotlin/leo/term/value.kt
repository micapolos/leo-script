package leo.term

import leo.Empty
import leo.named.value.anyScriptLine

sealed class Value<out T> {
  override fun toString() = scriptLine { anyScriptLine }.toString()
}

data class EmptyValue<T>(val empty: Empty): Value<T>() {
  override fun toString() = super.toString()
}

data class NativeValue<T>(val native: T) : Value<T>() {
  override fun toString() = super.toString()
}

data class FunctionValue<T>(val function: Function<T>) : Value<T>() {
  override fun toString() = super.toString()
}

data class Function<out T>(val scope: Scope<T>, val term: Term<T>) {
  override fun toString() = scriptLine { anyScriptLine }.toString()
}

@Suppress("UNCHECKED_CAST")
fun <T> Value<T>.invokeEvaluation(value: Value<T>): Evaluation<T, Value<T>> =
  when (this) {
    is EmptyValue -> null!!
    is FunctionValue -> function.scope.plus(value).valueEvaluation(function.term)
    is NativeValue -> null!!
  }

fun <T> value(empty: Empty): Value<T> = EmptyValue(empty)
fun <T> nativeValue(native: T): Value<T> = NativeValue(native)
fun <T> value(function: Function<T>): Value<T> = FunctionValue(function)
fun <T> function(scope: Scope<T>, term: Term<T>) = Function(scope, term)

val <T> Value<T>.native: T get() = (this as NativeValue).native

val <T> T.nativeValue: Value<T> get() = NativeValue(this)

val Any?.anyValue: Value<Any?> get() = nativeValue
val Any?.anyString: String get() = (this as String)
val Any?.anyDouble: Double get() = (this as Double)

fun <T> idValue(): Value<T> = value(function(scope(), term(variable(0))))

val <T> Value<T>.eitherFirst: Value<T> get() = value(function(scope(this), fn(get<T>(1).invoke(get(2)))))
val <T> Value<T>.eitherSecond: Value<T> get() = value(function(scope(this), fn(get<T>(0).invoke(get(2)))))

val <T> Value<T>.functionOrNull: Function<T>? get() = (this as? FunctionValue)?.function

fun <T> Boolean.isValue(): Value<T> = idValue<T>().let { if (this) it.eitherFirst else it.eitherSecond }