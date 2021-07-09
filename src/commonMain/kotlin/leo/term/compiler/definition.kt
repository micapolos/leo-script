package leo.term.compiler

import leo.TypeFunction
import leo.base.notNullIf
import leo.term.IndexVariable
import leo.term.invoke
import leo.term.term
import leo.term.typed.TypedTerm
import leo.term.typed.typed

data class Definition(val function: TypeFunction)

fun definition(function: TypeFunction) = Definition(function)

fun <V> Definition.resolveOrNull(variable: IndexVariable, typedTerm: TypedTerm<V>): TypedTerm<V>? =
  notNullIf(typedTerm.t == function.lhsType) {
    typed(term<V>(variable).invoke(typedTerm.v), function.rhsType)
  }