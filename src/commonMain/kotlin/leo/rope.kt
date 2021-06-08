package leo

data class Rope<out T>(
	val tail: Stack<T>,
	val current: T,
	val head: Stack<T>)

val <T> Stack<T>.ropeOrNull: Rope<T>? get() =
	linkOrNull?.rope

val <T> StackLink<T>.rope: Rope<T> get() =
	Rope(tail, head, stack())

val <T> Rope<T>.backOrNull: Rope<T>? get() =
	when (tail) {
		is EmptyStack -> null
		is LinkStack -> Rope(tail.link.tail, tail.link.head, head.push(current))
	}

val <T> Rope<T>.forwardOrNull: Rope<T>? get() =
	when (head) {
		is EmptyStack -> null
		is LinkStack -> Rope(tail.push(current), head.link.head, head.link.tail)
	}

val <T: Any> Rope<T>.previousOrNull: T? get() = backOrNull?.current
val <T: Any> Rope<T>.nextOrNull: T? get() = forwardOrNull?.current

val <T> Rope<T>.stackLink: StackLink<T> get() =
	(tail linkTo current).fold(head) { push(it) }

val <T> Rope<T>.stack: Stack<T> get() =
	stackLink.stack

val <T> Rope<T>?.orNullStack: Stack<T> get() =
	this?.stackLink?.stack ?: stack()

fun <T, O> Rope<T>.map(fn: (T) -> O): Rope<O> =
	Rope(tail.map(fn), fn(current), head.map(fn))

fun <T> Rope<T>.updateCurrent(fn: (T) -> T): Rope<T> =
	Rope(tail, fn(current), head)

tailrec fun <F, V> F.fold(rope: Rope<V>, fn: F.(Rope<V>) -> F): F {
	val folded = fn(rope)
	val previousOrNull = rope.backOrNull
	return if (previousOrNull == null) folded
	else folded.fold(previousOrNull, fn)
}

fun <V, O> Stack<V>.mapRope(fn: (Rope<V>) -> O): Stack<O> =
	ropeOrNull
		?.let { rope ->
			stack<O>()
				.fold(rope) { value -> push(fn(value)) }
				.reverse
		}
		?: stack()
