package leo

import leo.base.Effect
import leo.base.effect

data class Stateful<S, out V>(
	val run: (S) -> Effect<S, V>
)

fun <S, T> Stateful<S, T>.get(state: S): T =
	run(state).value

fun <S, V> V.stateful() = Stateful<S, V> { map -> map effect this }

fun <S, V, O> Stateful<S, V>.bind(fn: (V) -> Stateful<S, O>): Stateful<S, O> =
	Stateful { map ->
		run(map).let { mapToV ->
			fn(mapToV.value).let { statefulO ->
				statefulO.run(mapToV.state)
			}
		}
	}

fun <S, V, O> Stateful<S, V?>.nullableBind(fn: (V) -> Stateful<S, O>): Stateful<S, O?> =
	bind {
		if (it == null) null.stateful<S, O?>()
		else fn(it)
	}

fun <S, V> Stateful<S, V?>.or(fn: () -> Stateful<S, V>): Stateful<S, V> =
	bind { it?.stateful() ?: fn() }

fun <S, V, O> Stateful<S, V>.map(fn: (V) -> O): Stateful<S, O> =
	bind { fn(it).stateful<S, O>() }

fun <S, V, O> Stateful<S, V?>.nullableMap(fn: (V) -> O): Stateful<S, O?> =
	nullableBind { fn(it).stateful<S, O>() }

fun <S, T> Stateful<S, T>.catch(fn: (Throwable) -> Stateful<S, T>): Stateful<S, T> =
	Stateful { state ->
		try {
			run(state)
		} catch (throwable: Throwable) {
			fn(throwable).run(state)
		}
	}

val <S, T> Stack<Stateful<S, T>>.flat: Stateful<S, Stack<T>> get() =
	stack<T>().stateful<S, Stack<T>>().fold(this) { statefulValue ->
		bind { stack ->
			statefulValue.bind { value ->
				stack.push(value).reverse.stateful()
			}
		}
	}