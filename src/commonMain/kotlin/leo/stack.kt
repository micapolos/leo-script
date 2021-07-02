package leo

import leo.base.Seq
import leo.base.SeqNode
import leo.base.appendableString
import leo.base.emptySeq
import leo.base.fail
import leo.base.fold
import leo.base.ifNotNull
import leo.base.notNullIf
import leo.base.orNull
import leo.base.orNullIf
import leo.base.runIf
import leo.base.seq
import leo.base.the
import leo.base.then

sealed class Stack<out T>

data class EmptyStack<out T>(
	val empty: Empty) : Stack<T>() {
	override fun toString() = "stack"
}

data class LinkStack<out T>(
	val link: StackLink<T>) : Stack<T>() {
	override fun toString() = "${link.tail}.push(${link.head})"
}

data class StackLink<out T>(
	val tail: Stack<T>,
	val head: T) {
	override fun toString() = "link($tail, $head)"
}

// ------------

fun <T> stack(empty: Empty): Stack<T> = EmptyStack(empty)
fun <T> stack(pop: StackLink<T>): Stack<T> = LinkStack(pop)
fun <T> stack(vararg values: T): Stack<T> = stack<T>(empty).fold(values) { push(it) }
fun <T> stackOf(vararg values: T): Stack<T> = stack<T>(empty).fold(values) { push(it) }
fun <T> stackLink(value: T, vararg values: T) = link(stack(), value).fold(values) { push(it) }
fun <T> nonEmptyStack(value: T, vararg values: T) = stack(stackLink(value, *values))
fun <T> link(tail: Stack<T>, head: T) = StackLink(tail, head)
fun <T> Stack<T>.push(value: T) = stack(link(this, value))
infix fun <T> Stack<T>.linkTo(value: T) = link(this, value)
fun <T> Stack<T>.pushAll(vararg values: T): Stack<T> = fold(values) { push(it) }
fun <T> Stack<T>.pushAll(stack: Stack<T>): Stack<T> = fold(stack.reverse) { push(it) }
fun <T> Stack<T>.pushAllReversed(stack: Stack<T>): Stack<T> = fold(stack) { push(it) }
fun <T> StackLink<T>.push(value: T) = link(stack(this), value)
val <T> Stack<T>.emptyOrNull get() = (this as? EmptyStack)?.empty
val <T> Stack<T>.linkOrNull get() = (this as? LinkStack)?.link
val <T> Stack<T>.splitOrNull get() = (this as? LinkStack)?.link?.run { tail to head }
val <T> Stack<T>.onlyLinkOrNull get() = linkOrNull?.run { orNullIf(!link.tail.isEmpty) }
val <T : Any> Stack<T>.valueOrNull: T? get() = linkOrNull?.head
val <T> Stack<T>.link get() = linkOrNull!!
val <T> Stack<T>.pop get() = link.tail
val <T> Stack<T>.top get() = link.head
val <T> Stack<T>.topOrNull: T? get() = linkOrNull?.head
val <T: Any> Stack<T>.isSingleton: Boolean get() = onlyOrNull != null
val <T : Any> Stack<T>.onlyOrNull
	get() = linkOrNull?.let { link ->
		notNullIf(link.tail.isEmpty) {
			link.head
		}
	}
val <T> StackLink<T>.stack: Stack<T> get() = stack(this)

val <T> T.onlyStack get() = stack(this)
val <T> T.stackLink get() = stack<T>().linkTo(this)

val <T : Any> StackLink<T>.onlyOrNull get() = notNullIf(tail.isEmpty) { head }

tailrec fun <R, T> R.fold(stack: Stack<T>, fn: R.(T) -> R): R =
	when (stack) {
		is EmptyStack -> this
		is LinkStack -> fn(stack.link.head).fold(stack.link.tail, fn)
	}

fun <R, T> R.foldRight(stack: Stack<T>, fn: R.(T) -> R): R =
	fold(stack.reverse, fn)

val <T> Stack<T>.reverse get() = stack<T>().fold(this) { push(it) }
val <T> StackLink<T>.reverse get() = stackLink(head).fold(tail) { push(it) }
inline val Stack<*>.isEmpty get() = this is EmptyStack

fun <T> Stack<T>.any(fn: T.() -> Boolean): Boolean =
	false.fold(this) { or(fn(it)) }

fun <T> Stack<T>.all(fn: T.() -> Boolean): Boolean =
	true.fold(this) { and(fn(it)) }

fun <T> Stack<T>.contains(value: T): Boolean =
	any { this == value }

fun <T, R> Stack<T>.map(fn: T.() -> R): Stack<R> =
	reverseMap(fn).reverse

fun <T, R> Stack<T>.mapIt(fn: (T) -> R): Stack<R> =
	reverseMap(fn).reverse

fun <T, R> StackLink<T>.map(fn: T.() -> R): StackLink<R> =
	tail.map(fn).linkTo(head.fn())

fun <T, R> Stack<T>.reverseMap(fn: T.() -> R): Stack<R> =
	stack<R>().fold(this) { push(fn(it)) }

fun <T> StackLink<T>.updateTop(fn: T.() -> T): StackLink<T> =
	tail linkTo (head.fn())

tailrec fun <T, R : Any> Stack<T>.mapFirst(fn: T.() -> R?): R? =
	when (this) {
		is EmptyStack -> null
		is LinkStack -> link.head.fn() ?: link.tail.mapFirst(fn)
	}

fun <T : Any> Stack<T>.first(fn: (T) -> Boolean): T? =
	mapFirst { notNullIf(fn(this)) { this } }

fun <T: Any> Stack<T>.updateFirst(fn: T.() -> T?): Stack<T>? =
	when (this) {
		is EmptyStack -> null
		is LinkStack -> link.head.fn()
			?.let { link.tail.push(it) }
			?: link.tail.updateFirst(fn)?.push(link.head)
	}

fun <T, R : Any> Stack<T>.mapOnly(fn: T.() -> R?): R? =
	the(null as R?).orNull.fold(this) { value ->
		this?.let { theOnlyOrNull ->
			value.fn().let { mapped ->
				if (mapped == null) theOnlyOrNull
				else if (theOnlyOrNull.value == null) the(mapped)
				else null
			}
		}
	}?.value

fun <T, R> Stack<T>.flatMap(fn: T.() -> Stack<R>): Stack<R> =
	stack<R>().fold(this) { value ->
		fold(value.fn()) { mappedValue ->
			push(mappedValue)
		}
	}.reverse

fun <T, R : Any> Stack<T>.mapOrNull(fn: T.() -> R?): Stack<R>? =
	stack<R>().orNull.fold(this) { value ->
		this?.run {
			value.fn()?.let { mapped -> push(mapped) }
		}
	}?.reverse

tailrec fun <T> Stack<T>.get(int: Int): T? =
	when (this) {
		is EmptyStack -> null
		is LinkStack -> if (int == 0) link.head else link.tail.get(int.dec())
	}

fun <T> Stack<T>.getFromBottom(int: Int): T? =
	get(size - int - 1)

tailrec fun <T> Stack<T>.unsafeGet(int: Int): T =
	when (this) {
		is EmptyStack -> fail()
		is LinkStack -> if (int == 0) link.head else link.tail.unsafeGet(int.dec())
	}

tailrec fun <T> Stack<T>.drop(stack: Stack<*>): Stack<T>? =
	when (stack) {
		is EmptyStack -> this
		is LinkStack ->
			when (this) {
				is EmptyStack -> null
				is LinkStack -> link.tail.drop(stack.link.tail)
			}
	}

tailrec fun <A : Any, B : Any, R> R.zipFold(stackA: Stack<A>, stackB: Stack<B>, fn: R.(A?, B?) -> R): R =
	when (stackA) {
		is EmptyStack ->
			when (stackB) {
				is EmptyStack -> this
				is LinkStack -> fn(null, stackB.link.head).zipFold(stackA, stackB.link.tail, fn)
			}
		is LinkStack ->
			when (stackB) {
				is EmptyStack -> fn(stackA.link.head, null).zipFold(stackA.link.tail, stackB, fn)
				is LinkStack -> fn(stackA.link.head, stackB.link.head).zipFold(stackA.link.tail, stackB.link.tail, fn)
			}
	}

fun <A : Any, B : Any, R : Any> R.zipFoldOrNull(stackA: Stack<A>, stackB: Stack<B>, fn: R.(A, B) -> R): R? =
	orNull.zipFold(stackA, stackB) { aOrNull, bOrNull ->
		this?.run {
			if (aOrNull != null && bOrNull != null) fn(aOrNull, bOrNull)
			else null
		}
	}

fun <A : Any, B : Any> zip(stackA: Stack<A>, stackB: Stack<B>): Stack<Pair<A?, B?>> =
	stack<Pair<A?, B?>>().zipFold(stackA, stackB) { a, b -> push(a to b) }.reverse

fun <A, B, R> zipMapOrNull(aStack: Stack<A>, bStack: Stack<B>, fn: (A, B) -> R): Stack<R>? =
	stack<R>().orNull.zipFold(aStack.map { the }, bStack.map { the }) { theAOrNull, theBOrNull ->
		this?.run {
			if (theAOrNull != null && theBOrNull != null) push(fn(theAOrNull.value, theBOrNull.value))
			else null
		}
	}?.reverse

val <A : Any> Stack<A?>.filterNulls: Stack<A>
	get() =
		stack<A>().fold(this) { ifNotNull(it) { value -> push(value) } }

val <V> Stack<V>.seq: Seq<V>
	get() =
		when (this) {
			is EmptyStack -> emptySeq()
			is LinkStack -> link.seqNode.seq
		}

val <V> StackLink<V>.seqNode: SeqNode<V>
	get() =
		head then tail.seq

fun <V> Stack<V>.toString(valueToString: (V) -> String): String =
	appendableString { appendable ->
		appendable.fold(this) { value ->
			append(valueToString(value))
		}
	}

val <V> Stack<V>.deduplicate: Stack<V>
	get() =
		stack<V>().fold(this) {
			runIf(!contains(it)) { push(it) }
		}.reverse

val <V> Stack<V>.containsDistinct: Boolean
	get() =
		deduplicate == this

fun <V> Stack<V>.filter(fn: V.() -> Boolean): Stack<V> =
	stack<V>().fold(this) { if (it.fn()) push(it) else this }.reverse

operator fun <V : Any> Stack<V>.component1(): V? =
	linkOrNull?.head

operator fun <V : Any> Stack<V>.component2(): V? =
	linkOrNull?.tail?.linkOrNull?.head

operator fun <V : Any> Stack<V>.component3(): V? =
	linkOrNull?.tail?.linkOrNull?.tail?.linkOrNull?.head

operator fun <V : Any> Stack<V>.component4(): V? =
	linkOrNull?.tail?.linkOrNull?.tail?.linkOrNull?.tail?.linkOrNull?.head

fun <V, R : Any> Stack<V>.map1OrNull(fn: (V) -> R): R? =
	linkOrNull?.let { link0 ->
		link0.tail.emptyOrNull?.run {
			fn(link0.head)
		}
	}

fun <V, R : Any> Stack<V>.map2OrNull(fn: (V, V) -> R): R? =
	linkOrNull?.let { link0 ->
		link0.tail.linkOrNull?.let { link1 ->
			link1.tail.emptyOrNull?.run {
				fn(link1.head, link0.head)
			}
		}
	}

fun <V, R : Any> Stack<V>.mapOrNull2OrNull(fn: (V, V) -> R?): R? =
	linkOrNull?.let { link0 ->
		link0.tail.linkOrNull?.let { link1 ->
			link1.tail.emptyOrNull?.run {
				fn(link1.head, link0.head)
			}
		}
	}

fun <V, R : Any> Stack<V>.map8OrNull(fn: (V, V, V, V, V, V, V, V) -> R): R? =
	linkOrNull?.let { link0 ->
		link0.tail.linkOrNull?.let { link1 ->
			link1.tail.linkOrNull?.let { link2 ->
				link2.tail.linkOrNull?.let { link3 ->
					link3.tail.linkOrNull?.let { link4 ->
						link4.tail.linkOrNull?.let { link5 ->
							link5.tail.linkOrNull?.let { link6 ->
								link6.tail.linkOrNull?.let { link7 ->
									link7.tail.emptyOrNull?.run {
										fn(
											link7.head,
											link6.head,
											link5.head,
											link4.head,
											link3.head,
											link2.head,
											link1.head,
											link0.head)
									}
								}
							}
						}
					}
				}
			}
		}
	}

fun <V> Stack<V>.toReverseList(): List<V> =
	mutableListOf<V>().fold(this) { item -> also { it.add(item) } }.toList()

fun <V> Stack<V>.toList(): List<V> =
	reverse.toReverseList()

inline val <reified V> Stack<V>.array: Array<V>
	get() =
		toList().toTypedArray()

val Stack<String>.stringJoin: String get() =
	array.joinToString("")

val Stack<*>.size
	get() =
		0.fold(this) { inc() }

fun <V, R> Stack<V>.split(fn: (Stack<V>, V) -> R): R? =
	linkOrNull?.run { fn(tail, head) }

val Stack<Char>.charString: String get() = array.toCharArray().concatToString()

fun <T> StackLink<T>.updateValue(fn: (T) -> T): StackLink<T> =
	StackLink(tail, fn(head))

// TODO: Refactor it to be tail recursive
fun <T: Any> Stack<T>.updateFirstOrNull(fn: (T) -> T?): Stack<T>? =
	when (this) {
		is EmptyStack -> null
		is LinkStack -> {
			val updated = fn(link.head)
			if (updated != null) stack(link.tail linkTo updated)
			else {
				val stack = link.tail.updateFirstOrNull(fn)
				if (stack != null) stack(stack linkTo link.head)
				else null
			}
		}
	}
