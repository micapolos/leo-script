package leo.typed.compiler

import leo.Stack
import leo.base.mapFirstOrNull
import leo.base.mapIndexed
import leo.mapFirst
import leo.push
import leo.seq
import leo.stack
import leo.typed.compiled.Compiled
import leo.typed.compiled.CompiledChoice
import leo.variable

data class Scope(val bindingStack: Stack<Binding>)

fun scope() = Scope(stack())

fun Scope.plus(binding: Binding): Scope =
  bindingStack.push(binding).let(::Scope)

fun <V> Scope.resolveOrNull(compiled: Compiled<V>): Compiled<V>? =
  bindingStack.seq.mapIndexed.mapFirstOrNull { value.resolveOrNull(variable(index), compiled) }

fun <V> Scope.resolve(compiled: Compiled<V>): Compiled<V> =
  resolveOrNull(compiled) ?: compiled

fun <V> Scope.compiledChoice(): CompiledChoice<V> =
  bindingStack.mapFirst { compiledChoiceOrNull() }!!
