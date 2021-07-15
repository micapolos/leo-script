package leo.term.compiler

import leo.Script
import leo.Type
import leo.base.nullOf
import leo.fold
import leo.lineStack
import leo.matchPrefix
import leo.recursiveName
import leo.reverse
import leo.term.compiled.Body
import leo.term.compiled.Compiled
import leo.term.compiled.CompiledSelect
import leo.term.compiled.body
import leo.term.compiled.compiled
import leo.term.compiled.compiledSelect
import leo.term.compiled.recursive
import leo.term.compiler.native.Native
import leo.term.compiler.native.nativeEnvironment
import leo.type

data class Module<V>(
  val context: Context<V>,
  val typeContext: Context<Native> /* Should be Context<Type>? */)

val <V> Context<V>.module: Module<V> get() =
  Module(this, nativeEnvironment.context)

fun <V> Module<V>.plus(binding: Binding): Module<V> =
  copy(context = context.plus(binding))

fun <V> Module<V>.bind(type: Type): Module<V> =
  copy(context = context.bind(type))

fun <V> Module<V>.type(script: Script): Type =
  script.type // TODO: Evaluate using typeContext

fun <V> Module<V>.compiledSelectOrNull(script: Script): CompiledSelect<V>? =
  nullOf<SelectCompiler<V>>().fold(script.lineStack.reverse) { scriptLine ->
    null
      ?: this?.plus(scriptLine)
      ?: SelectCompiler(this@compiledSelectOrNull, compiledSelect()).plusOrNull(scriptLine)
  }?.compiledSelect

fun <V> Module<V>.compiled(script: Script): Compiled<V> =
  null
    ?: compiledSelectOrNull(script)?.compiled
    ?: local.compiler.plus(script).completeCompiled

fun <V> Module<V>.body(script: Script): Body<V> =
  script.matchPrefix(recursiveName) { recursiveScript ->
    recursive(body(recursiveScript))
  }?: body(this.compiled(script))

