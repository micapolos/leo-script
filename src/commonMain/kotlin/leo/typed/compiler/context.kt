package leo.typed.compiler

import leo.typed.compiled.Compiled

data class Context<V>(
  val environment: Environment<V>,
  val scope: Scope
)

val <V> Environment<V>.context
  get() =
    Context(this, scope())

fun <V> Context<V>.plus(binding: Binding): Context<V> =
  copy(scope = scope.plus(binding))

fun <V> Context<V>.resolve(compiled: Compiled<V>): Compiled<V> =
  null
    ?: scope.resolveOrNull(compiled)
    ?: environment.resolveTypeOrNull(compiled)
    ?: environment.resolveOrNullFn(compiled)
    ?: compiled.resolvedOrNull
    ?: compiled
