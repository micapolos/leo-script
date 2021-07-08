package leo.term.compiler

import leo.Script
import leo.ScriptLine
import leo.Type
import leo.base.notNullOrError
import leo.fieldOrNull
import leo.onlyLineOrNull
import leo.term.typed.TypedSelection
import leo.term.typed.TypedTerm
import leo.term.typed.noSelection
import leo.term.typed.onlyLineOrNull
import leo.term.typed.yesSelection

data class Context<V>(
  val environment: Environment<V>,
  val scope: Scope
)

val <V> Environment<V>.context
  get() =
    Context(this, scope())

fun <V> Context<V>.typedTerm(script: Script): TypedTerm<V> =
  module.compiler.plus(script).compiledTypedTerm

fun <V> Context<V>.plus(binding: Binding): Context<V> =
  copy(scope = scope.plus(binding))

fun <V> Context<V>.resolve(typedTerm: TypedTerm<V>): TypedTerm<V> =
  null
    ?: scope.resolveOrNull(typedTerm)
    ?: environment.resolveTypeOrNull(typedTerm)
    ?: environment.resolveOrNullFn(typedTerm)
    ?: typedTerm.resolvedOrNull
    ?: typedTerm

fun <V> Context<V>.type(script: Script): Type =
  typedTerm(script).type

fun <V> Context<V>.typedSelection(scriptLine: ScriptLine): TypedSelection<V> =
  scriptLine.fieldOrNull.notNullOrError("$scriptLine.conditional").let { field ->
    field.name.selectBoolean.let { boolean ->
      if (boolean) yesSelection(typedTerm(field.rhs).onlyLineOrNull.notNullOrError("dupa i już"))
      else noSelection(type(field.rhs).onlyLineOrNull.notNullOrError("dupa kabana"))
    }
  }
