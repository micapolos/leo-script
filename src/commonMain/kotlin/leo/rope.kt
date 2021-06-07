package leo

data class Rope<out T>(
	val tail: Stack<T>,
	val current: T,
	val head: Stack<T>)

val <T> Stack<T>.ropeOrNull: Rope<T>? get() =
	linkOrNull?.rope

val <T> StackLink<T>.rope: Rope<T> get() =
	Rope(tail, head, stack())

val <T> Rope<T>.previousOrNull: Rope<T>? get() =
	when (tail) {
		is EmptyStack -> null
		is LinkStack -> Rope(tail.link.tail, tail.link.head, head.push(current))
	}

val <T> Rope<T>.nextOrNull: Rope<T>? get() =
	when (head) {
		is EmptyStack -> null
		is LinkStack -> Rope(tail.push(current), head.link.head, head.link.tail)
	}

val <T> Rope<T>.stackLink: StackLink<T> get() =
	(tail linkTo current).fold(head) { push(it) }

val <T> Rope<T>.stack: Stack<T> get() =
	stackLink.stack

fun <T, O> Rope<T>.map(fn: (T) -> O): Rope<O> =
	Rope(tail.map(fn), fn(current), head.map(fn))