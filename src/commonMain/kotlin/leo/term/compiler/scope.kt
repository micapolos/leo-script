package leo.term.compiler

import leo.EmptyStack
import leo.LinkStack
import leo.Stack
import leo.push
import leo.stack
import leo.term.IndexVariable
import leo.term.compiled.Compiled
import leo.term.variable

data class Scope(val bindingStack: Stack<Binding>)

fun scope() = Scope(stack())

fun Scope.plus(binding: Binding): Scope =
  bindingStack.push(binding).let(::Scope)

fun <V> Scope.resolveOrNull(compiled: Compiled<V>): Compiled<V>? =
  resolveOrNull(variable(0), compiled)

tailrec fun <V> Scope.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  when (bindingStack) {
    is EmptyStack -> null
    is LinkStack ->
      null
        ?: bindingStack.link.head.resolveOrNull(variable, compiled)
        ?: Scope(bindingStack.link.tail).resolveOrNull(variable(variable.index.plus(bindingStack.link.head.indexCount)), compiled)
  }

fun <V> Scope.resolve(compiled: Compiled<V>): Compiled<V> =
  resolveOrNull(compiled) ?: compiled

