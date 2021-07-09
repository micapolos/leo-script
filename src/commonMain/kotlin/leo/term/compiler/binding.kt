package leo.term.compiler

import leo.term.IndexVariable
import leo.term.typed.TypedTerm
import leo.term.typed.typedTerm

sealed class Binding
data class GivenBinding(val given: Given) : Binding()
data class DefinitionBinding(val definition: Definition) : Binding()

fun binding(given: Given): Binding = GivenBinding(given)
fun binding(definition: Definition): Binding = DefinitionBinding(definition)

fun <V> Binding.resolveOrNull(variable: IndexVariable, typedTerm: TypedTerm<V>): TypedTerm<V>? =
  when (this) {
    is DefinitionBinding -> definition.resolveOrNull(variable, typedTerm)
    is GivenBinding -> given.resolveOrNull(variable, typedTerm)
  }

fun <V> Binding.getOrNull(variable: IndexVariable, name: String): TypedTerm<V>? =
  resolveOrNull(variable, typedTerm(name))
