package leo.base.stak

import leo.base.appendableString
import leo.base.fold
import leo.base.map
import leo.base.reverse
import leo.script

// Stack with binary-replicated links.
// push = O(log(n))
// pop = O(1)
// pop(n) = O(log(n))
// top = O(1)
// size = O(log(n))
// get(index) = O(log(n))
data class Stak<out T : Any>(
	val nodeOrNull: Node<T>?) {
	//	override fun toString() = scriptLine { script(toString()) }.toString()
	override fun toString() = appendableString { it.append(this) }
}

data class Node<out T : Any>(
	val value: T,
	val linkOrNull: Link<T>?) {
	override fun toString() = scriptLine { script(toString()) }.toString()
}

data class Link<out T : Any>(
	val node: Node<T>,
	val linkOrNull: Link<T>?) {
	override fun toString() = scriptLine { script(toString()) }.toString()
}

fun <T : Any> stak(nodeOrNull: Node<T>?) = Stak(nodeOrNull)
fun <T : Any> link(node: Node<T>, linkOrNull: Link<T>?) = Link(node, linkOrNull)
fun <T : Any> node(value: T, linkOrNull: Link<T>?) = Node(value, linkOrNull)

fun <T : Any> emptyStak(): Stak<T> = stak(null)
fun <T : Any> stakOf(vararg values: T): Stak<T> =
	emptyStak<T>().fold(values) { push(it) }

val Stak<*>.isEmpty: Boolean get() = nodeOrNull == null

val <T : Any> Stak<T>.top: T?
	get() =
		nodeOrNull?.value

val <T : Any> Stak<T>.unlink: Pair<Stak<T>, T>?
	get() =
		nodeOrNull?.let { node ->
			stak(node.linkOrNull?.node) to node.value
		}

fun <T : Any> Stak<T>.top(index: Int): T? =
	nodeOrNull?.top(index)

fun <T : Any> Stak<T>.bottom(index: Int): T? =
	top(size - index - 1)

val <T : Any> Stak<T>.pop: Stak<T>?
	get() =
		nodeOrNull?.let { stak(it.pop) }

fun <T : Any> Stak<T>.pop(count: Int): Stak<T>? =
	nodeOrNull?.let { stak(it.pop(count)) }

fun <T : Any> Stak<T>.push(value: T): Stak<T> =
	stak(nodeOrNull.push(value))

val <T : Any> Stak<T>.size: Int
	get() =
		nodeOrNull?.size ?: 0

operator fun <T : Any> Stak<T>.get(index: Int): T? =
	top(size - index - 1)

fun <T : Any> Node<T>.top(index: Int): T? =
	pop(index)?.value

val <T : Any> Node<T>.pop: Node<T>?
	get() =
		linkOrNull?.node

//fun <T : Any> Node<T>.pop(count: Int): Node<T>? =
//	if (count == 0) this
//	else linkOrNull?.pop(count, 1)
//
//fun <T : Any> Link<T>.pop(count: Int, depth: Int): Node<T>? {
//	val newCount = count - depth
//	return if (newCount > 0)
//		if (linkOrNull == null) node.pop(newCount)
//		else if (count - depth.shl(1) >= 0) linkOrNull.pop(count, depth.shl(1))
//		else node.pop(newCount)
//	else
//		if (newCount == 0) node
//		else null
//}

fun <T : Any> Node<T>.pop(count: Int): Node<T>? =
	if (count == 0) this
	else linkOrNull?.pop(count)


fun <T : Any> Link<T>.pop(count: Int): Node<T>? {
	var countVar = count
	var depth = 1
	var link: Link<T>? = this
	while (true) {
		if (link == null) return null
		if (countVar == depth) return link.node
		val nextLink = link.linkOrNull
		if (nextLink == null) {
			countVar -= depth
			depth = 1
			link = link.node.linkOrNull
		} else {
			val nextDepth = depth.shl(1)
			if (countVar <= nextDepth) {
				countVar -= depth
				depth = 1
				link = link.node.linkOrNull
			} else {
				depth = nextDepth
				link = link.linkOrNull
			}
		}
	}
}

tailrec fun <T : Any> Link<T>.leafNode(depth: Int): Node<T>? =
	if (depth == 1 && linkOrNull == null) node
	else linkOrNull?.leafNode(depth.ushr(1))

fun <T : Any> Node<T>.pushLink(depth: Int): Link<T>? {
	return if (linkOrNull == null) link(this, null)
	else {
		val leafNode = linkOrNull.leafNode(depth)
		return if (leafNode == null) link(this, null)
		else link(this, leafNode.pushLink(depth.shl(1)))
	}
}

fun <T : Any> Node<T>?.push(value: T): Node<T> =
	if (this == null) node(value, null)
	else node(value, this.pushLink(1))

tailrec fun <R, T : Any> R.fold(node: Node<T>, fn: R.(T) -> R): R {
	val folded = fn(node.value)
	return if (node.linkOrNull == null) folded
	else folded.fold(node.linkOrNull.node, fn)
}

val <T : Any> Node<T>.size: Int
	get() =
		linkOrNull?.size ?: 1

val <T : Any> Link<T>.size: Int
	get() {
		var size = 1
		var depth = 1
		var link = this
		while (true) {
			val nextLink = link.linkOrNull
			if (nextLink != null) {
				depth = depth.shl(1)
				link = nextLink
			} else {
				size += depth
				val nextNodeLink = link.node.linkOrNull
				if (nextNodeLink == null) break
				else {
					depth = 1
					link = nextNodeLink
				}
			}
		}
		return size
	}

fun <T : Any> Appendable.append(stak: Stak<T>): Appendable =
	append("stak").fold(stak.reverse) { append(".push(").append(it.toString()).append(")") }

fun <T : Any, R : Any> Stak<T>.indexedTop(fn: Int.(T) -> R?): R? {
	var index = 0
	var stak = this
	while (true) {
		val pair = stak.unlink ?: break
		val result = index.fn(pair.second)
		if (result != null) return result
		index++
		stak = pair.first
	}
	return null
}

fun <T : Any, R : Any> Stak<T>.map(f: T.() -> R): Stak<R> =
	emptyStak<R>().fold(seq.map(f).reverse) { push(it) }
