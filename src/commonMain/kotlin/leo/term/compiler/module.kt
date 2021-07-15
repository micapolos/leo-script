package leo.term.compiler

import leo.Script
import leo.Type
import leo.base.nullOf
import leo.base.orIfNull
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
import leo.term.compiled.indexed.indexedExpression
import leo.term.compiled.recursive
import leo.term.compiler.native.Native
import leo.term.compiler.native.nativeEnvironment
import leo.term.indexed.native.nativeEvaluator
import leo.term.indexed.native.script
import leo.term.indexed.value
import leo.type

data class Module<V>(
  val context: Context<V>,
  val typeLocalOrNull: Local<Native>?)

val <V> Context<V>.module: Module<V> get() =
  Module(this, null)

fun <V, R> Module<V>.inTypeLocal(fn: (Local<Native>) -> R): R =
  fn(typeLocalOrNull.orIfNull { nativeEnvironment.context.module.local })

fun <V> Module<V>.updateTypeLocal(fn: (Local<Native>) -> Local<Native>): Module<V> =
  copy(typeLocalOrNull = inTypeLocal { fn(it) })

fun <V> Module<V>.plus(binding: Binding): Module<V> =
  copy(context = context.plus(binding))

fun <V> Module<V>.bind(type: Type): Module<V> =
  copy(context = context.bind(type))

fun <V> Module<V>.type(script: Script): Type =
  inTypeLocal { typeLocal ->
    typeLocal.compiler.plus(script).completeCompiled.let { compiled ->
      compiled.indexedExpression.value(nativeEvaluator).script(compiled.type).type
    }
  }

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

