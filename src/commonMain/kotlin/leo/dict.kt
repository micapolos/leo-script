package leo

import leo.base.Effect
import leo.base.Seq
import leo.base.effect
import leo.base.notNullIf
import leo.base.orIfNull

// TODO: Replace with high-performance persistent implementation (ie: using Trie).

@kotlin.jvm.JvmInline
value class Dict<K, V>(val stack: Stack<Pair<K, V>>) {
  fun put(pair: Pair<K, V>): Dict<K, V> =
    stack
      .updateFirstOrNull {
        notNullIf(it.first == pair.first) {
          it.first to pair.second
        }
      }
      .orIfNull { stack.push(pair) }
      .dict

  fun updateOrNull(key: K, fn: (V) -> V): Dict<K, V>? =
    stack
      .updateFirstOrNull { pair ->
        notNullIf(key == pair.first) {
          key to fn(pair.second)
        }
      }
      ?.dict

  fun get(key: K): V? = stack.first { it.first == key }?.second
  fun remove(key: K): Dict<K, V> = Dict(stack.filter { first != key })
}

fun <K, V> dict() = Dict<K, V>(stack())
val <K, V> Stack<Pair<K, V>>.dict get() = Dict(this)

val <K, V> Dict<K, V>.pairSeq: Seq<Pair<K, V>> get() = stack.seq
fun <K, V> Dict<K, V>.getOr(key: K, value: V): V = get(key) ?: value

fun <K, V : Any> Dict<K, V>.updateAndGetEffect(key: K, fn: (V?) -> V): Effect<Dict<K, V>, V> =
  fn(get(key)).let { put(key to it) effect it }
