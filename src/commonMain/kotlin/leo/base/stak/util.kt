package leo.base.stak

import leo.Stack
import leo.base.Seq
import leo.base.seq
import leo.base.then
import leo.push
import leo.reverse
import leo.stack
import leo.toReverseList

fun <R, T : Any> R.fold(stak: Stak<T>, fn: R.(T) -> R): R =
	if (stak.nodeOrNull == null) this
	else fold(stak.nodeOrNull, fn)

val <T : Any> Stak<T>.reverse
	get() =
		stakOf<T>().fold(this) { push(it) }

val <T : Any> Stak<T>.reverseStack: Stack<T>
	get() =
		stack<T>().fold(this) { push(it) }

val <T : Any> Stak<T>.stack
	get() =
		reverseStack.reverse

val <T : Any> Stak<T>.list
	get() =
		reverseStack.toReverseList()

fun <R : Any, T : Any> Pair<R, Stak<T>>.reduce(fn: R.(T) -> R): Pair<R, Stak<T>>? =
	second.unlink?.let { (stak, value) ->
		first.fn(value) to stak
	}

val <T : Any> Stak<T>.seq: Seq<T>
	get() =
		nodeOrNull.seq

val <T : Any> Node<T>?.seq: Seq<T>
	get() =
		seq { this?.run { value then linkOrNull?.node.seq } }