package leo.term.compiler

import leo.Stack
import leo.base.mapFirstOrNull
import leo.base.mapIndexed
import leo.base.notNullOrError
import leo.fold
import leo.push
import leo.seq
import leo.stack
import leo.term.compiled.Compiled
import leo.term.variable

data class Scope(val bindingStack: Stack<Binding>)

fun scope() = Scope(stack())

fun Scope.plus(binding: Binding): Scope =
  bindingStack.push(binding).let(::Scope)

fun <V> Scope.resolveOrNull(compiled: Compiled<V>): Compiled<V>? =
  bindingStack.seq.mapIndexed.mapFirstOrNull {
    value.resolveOrNull(variable(index), compiled)
  }

fun <V> Scope.get(name: String): Compiled<V> =
  bindingStack.seq.mapIndexed.mapFirstOrNull<IndexedValue<Binding>, Compiled<V>> {
    value.getOrNull(variable(index), name)
  }.notNullOrError("$this get $name")

fun <V> Scope.invoke(get: Get): Compiled<V> =
  get<V>(get.nameStackLink.head).fold(get.nameStackLink.tail) { get(it) }

fun <V> Scope.resolve(compiled: Compiled<V>): Compiled<V> =
  resolveOrNull(compiled) ?: compiled

