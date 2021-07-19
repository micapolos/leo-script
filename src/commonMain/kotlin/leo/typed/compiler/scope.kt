package leo.typed.compiler

import leo.Stack
import leo.mapFirst
import leo.push
import leo.stack
import leo.typed.compiled.Compiled

data class Scope(val bindingStack: Stack<Binding>)

fun scope() = Scope(stack())

fun Scope.plus(binding: Binding): Scope =
  bindingStack.push(binding).let(::Scope)

fun <V> Scope.resolveOrNull(compiled: Compiled<V>): Compiled<V>? =
  bindingStack.mapFirst { resolveOrNull(compiled) }

fun <V> Scope.resolve(compiled: Compiled<V>): Compiled<V> =
  resolveOrNull(compiled) ?: compiled

