package leo.typed.compiler

import leo.Script
import leo.Type
import leo.Types
import leo.base.Seq
import leo.base.filterMap
import leo.base.mapFirstOrNull
import leo.base.orIfNull
import leo.base.the
import leo.matchPrefix
import leo.repeatingName
import leo.seq
import leo.staticScriptOrNull
import leo.type
import leo.typed.compiled.Body
import leo.typed.compiled.Compiled
import leo.typed.compiled.body
import leo.typed.compiled.castOrNull
import leo.typed.compiled.indexed.indexedExpression
import leo.typed.compiled.recursive
import leo.typed.indexed.script
import leo.typed.indexed.types.typesEvaluator
import leo.typed.indexed.types.typesValueScriptContext
import leo.typed.indexed.value

data class Module<V>(
  val context: Context<V>,
  val typesBlockOrNull: Block<Types>?)

val <V> Context<V>.module: Module<V> get() =
  Module(this, null)

fun <V, R> Module<V>.inTypesBlock(fn: (Block<Types>) -> R): R =
  fn(typesBlockOrNull.orIfNull { context.environment.typesNativeEnvironmentFn().context.module.block })

fun <V> Module<V>.updateTypesBlock(fn: (Block<Types>) -> Block<Types>): Module<V> =
  copy(typesBlockOrNull = inTypesBlock { fn(it) })

fun <V> Module<V>.plus(binding: Binding): Module<V> =
  copy(context = context.plus(binding))

fun <V> Module<V>.type(script: Script): Type =
  inTypesBlock { typeLocal ->
    typeLocal.compiler.plus(script).completeCompiled.let { compiled ->
      compiled.indexedExpression.value(typesEvaluator).script(compiled.type, typesValueScriptContext).type
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
  typeSeq.mapFirstOrNull {
    compiled.castOrNull(this)
  } ?: compiled

val <V> Module<V>.typeSeq: Seq<Type> get() =
  inTypesBlock { typesBlock ->
    typesBlock.module.context.scope.bindingStack.seq.filterMap {
      rhsType.staticScriptOrNull?.type?.the
    }
  }
