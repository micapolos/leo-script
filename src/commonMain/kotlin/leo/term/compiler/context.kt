package leo.term.compiler

import leo.Script
import leo.ScriptLine
import leo.Type
import leo.base.notNullOrError
import leo.fieldOrNull
import leo.onlyLineOrNull
import leo.term.compiled.Compiled
import leo.term.compiled.tupleOnlyLineOrNull
import leo.term.typed.TypedSelection
import leo.term.typed.noSelection
import leo.term.typed.yesSelection
import leo.type

data class Context<V>(
  val environment: Environment<V>,
  val scope: Scope
)

val <V> Environment<V>.context
  get() =
    Context(this, scope())

fun <V> Context<V>.compiled(script: Script): Compiled<V> =
  module.compiler.plus(script).completeCompiled

fun <V> Context<V>.plus(binding: Binding): Context<V> =
  copy(scope = scope.plus(binding))

fun <V> Context<V>.resolve(compiled: Compiled<V>): Compiled<V> =
  null
    ?: scope.resolveOrNull(compiled)
    ?: environment.resolveTypeOrNull(compiled)
    ?: environment.resolveOrNullFn(compiled)
    ?: compiled.resolvedOrNull
    ?: compiled

fun <V> Context<V>.type(script: Script): Type =
  script.type // TODO: Resolve

fun <V> Context<V>.typedSelection(scriptLine: ScriptLine): TypedSelection<V> =
  scriptLine.fieldOrNull.notNullOrError("$scriptLine.conditional").let { field ->
    field.name.selectBoolean.let { boolean ->
      if (boolean) yesSelection(compiled(field.rhs).tupleOnlyLineOrNull.notNullOrError("dupa i już"))
      else noSelection(type(field.rhs).onlyLineOrNull.notNullOrError("dupa kabana"))
    }
  }
