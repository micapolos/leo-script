package leo.typed.compiler

import leo.Script
import leo.Type
import leo.base.orIfNull
import leo.mapFirst
import leo.matchPrefix
import leo.repeatingName
import leo.type
import leo.typed.compiled.Body
import leo.typed.compiled.Compiled
import leo.typed.compiled.body
import leo.typed.compiled.castOrNull
import leo.typed.compiled.indexed.indexedExpression
import leo.typed.compiled.recursive
import leo.typed.compiler.native.Native
import leo.typed.indexed.native.nativeEvaluator
import leo.typed.indexed.native.script
import leo.typed.indexed.value

data class Module<V>(
  val context: Context<V>,
  val typesBlockOrNull: Block<Native>?)

val <V> Context<V>.module: Module<V> get() =
  Module(this, null)

fun <V, R> Module<V>.inTypesBlock(fn: (Block<Native>) -> R): R =
  fn(typesBlockOrNull.orIfNull { context.environment.typesNativeEnvironmentFn().context.module.block })

fun <V> Module<V>.updateTypesBlock(fn: (Block<Native>) -> Block<Native>): Module<V> =
  copy(typesBlockOrNull = inTypesBlock { fn(it) })

fun <V> Module<V>.plus(binding: Binding): Module<V> =
  copy(context = context.plus(binding))

fun <V> Module<V>.type(script: Script): Type =
  inTypesBlock { typeLocal ->
    typeLocal.compiler.plus(script).completeCompiled.let { compiled ->
      compiled.indexedExpression.value(nativeEvaluator).script(compiled.type).type
    }
  }

fun <V> Module<V>.compiled(script: Script): Compiled<V> =
  block.compiler.plus(script).completeCompiled

fun <V> Module<V>.body(script: Script): Body<V> =
  script.matchPrefix(repeatingName) { recursiveScript ->
    recursive(body(recursiveScript))
  }?: body(this.compiled(script))

fun <V> Module<V>.resolve(compiled: Compiled<V>): Compiled<V> =
  context.resolve(cast(compiled))

fun <V> Module<V>.cast(compiled: Compiled<V>): Compiled<V> =
  inTypesBlock { typesBlock ->
    typesBlock.bindingStack.mapFirst {
      compiled.castOrNull(this.compiled.indexedExpression.value(nativeEvaluator).script(this.compiled.type).type)
    } ?: compiled
  }

