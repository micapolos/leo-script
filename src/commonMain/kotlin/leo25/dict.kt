package leo25

import leo.base.Seq
import leo.base.notNullIf
import leo13.*

// TODO: Replace with high-performance persistent implementation (ie: using Trie).

data class Dict<K, V>(val stack: Stack<Pair<K, V>>)

fun <K, V> dict() = Dict<K, V>(stack())

fun <K, V> Dict<K, V>.put(pair: Pair<K, V>): Dict<K, V> =
	Dict(
		stack
			.updateFirstOrNull {
				notNullIf(it.first == pair.first) {
					it.first to pair.second
				}
			}
			?:stack.push(pair))

fun <K, V> Dict<K, V>.get(key: K): V? = stack.first { it.first == key }?.second
fun <K, V> Dict<K, V>.remove(key: K): Dict<K, V> = Dict(stack.filter { first != key })
val <K, V> Dict<K, V>.pairSeq: Seq<Pair<K, V>> get() = stack.seq
