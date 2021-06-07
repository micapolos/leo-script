package leo

data class Rope<out T>(
	val tail: Stack<T>,
	val current: T,
	val head: Stack<T>)

val <T> Stack<T>.ropeOrNull: Rope<T>? get() =
	linkOrNull?.rope

val <T> StackLink<T>.rope: Rope<T> get() =
	Rope(stack, value, stack())

val <T> Rope<T>.previousOrNull: Rope<T>? get() =
	when (tail) {
		is EmptyStack -> null
		is LinkStack -> Rope(tail.link.stack, tail.link.value, head.push(current))
	}

val <T> Rope<T>.nextOrNull: Rope<T>? get() =
	when (head) {
		is EmptyStack -> null
		is LinkStack -> Rope(tail.push(current), head.link.value, head.link.stack)
	}

val <T> Rope<T>.stackLink: StackLink<T> get() =
	(tail linkTo current).fold(head) { push(it) }

val <T> Rope<T>.stack: Stack<T> get() =
	stack(stackLink)

fun <T, O> Rope<T>.map(fn: (T) -> O): Rope<O> =
	Rope(tail.map(fn), fn(current), head.map(fn))