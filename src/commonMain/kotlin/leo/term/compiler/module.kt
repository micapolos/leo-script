package leo.term.compiler

import leo.Type
import leo.term.compiler.native.Native
import leo.term.compiler.native.nativeEnvironment

data class Module<V>(
  val context: Context<V>,
  val typeContext: Context<Native> /* Should be Context<Type>? */)

val <V> Context<V>.module: Module<V> get() =
  Module(this, nativeEnvironment.context)

fun <V> Module<V>.plus(binding: Binding): Module<V> =
  copy(context = context.plus(binding))

fun <V> Module<V>.bind(type: Type): Module<V> =
  copy(context = context.bind(type))

