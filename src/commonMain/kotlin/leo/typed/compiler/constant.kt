package leo.typed.compiler

import leo.IndexVariable
import leo.Type
import leo.base.ifOrNull
import leo.typed.compiled.Compiled
import leo.typed.compiled.compiled
import leo.typed.compiled.expression

data class Constant(val lhsType: Type, val rhsType: Type)

fun constant(lhsType: Type, rhsType: Type) = Constant(lhsType, rhsType)

fun <V> Constant.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  ifOrNull(lhsType == compiled.type) {
    compiled(expression(variable), rhsType)
  }

