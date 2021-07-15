package leo.term.compiler

import leo.Type
import leo.fold
import leo.name
import leo.named.compiler.compileStructure
import leo.reverse
import leo.term.compiled.Compiled
import leo.type

data class Context<V>(
  val environment: Environment<V>,
  val scope: Scope
)

val <V> Environment<V>.context
  get() =
    Context(this, scope())

fun <V> Context<V>.plus(binding: Binding): Context<V> =
  copy(scope = scope.plus(binding))

fun <V> Context<V>.bind(type: Type): Context<V> =
  fold(type.compileStructure.lineStack.reverse) { typeLine ->
    plus(binding(constant(type(typeLine.name), type(typeLine))))
  }

fun <V> Context<V>.resolve(compiled: Compiled<V>): Compiled<V> =
  null
    ?: scope.resolveOrNull(compiled)
    ?: environment.resolveTypeOrNull(compiled)
    ?: environment.resolveOrNullFn(compiled)
    ?: compiled.resolvedOrNull
    ?: compiled
