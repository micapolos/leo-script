package leo.term.compiler

import leo.Type
import leo.nameOrNull
import leo.term.IndexVariable
import leo.term.compiled.Compiled
import leo.term.compiled.compiled
import leo.term.compiled.expression
import leo.term.compiled.getOrNull

data class Given(val type: Type)

fun given(type: Type) = Given(type)

fun <V> Given.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  compiled.type.nameOrNull?.let { getOrNull(variable, it) }

fun <V> Given.getOrNull(variable: IndexVariable, name: String): Compiled<V>? =
  compiled(expression<V>(variable), type).getOrNull(name)
