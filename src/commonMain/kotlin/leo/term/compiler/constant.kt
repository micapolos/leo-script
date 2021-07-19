package leo.term.compiler

import leo.Type
import leo.base.ifOrNull
import leo.term.compiled.Compiled
import leo.term.compiled.compiled
import leo.term.compiled.expression
import leo.term.compiled.variable

data class Constant(val lhsType: Type, val rhsType: Type)

fun constant(lhsType: Type, rhsType: Type) = Constant(lhsType, rhsType)

fun <V> Constant.resolveOrNull(compiled: Compiled<V>): Compiled<V>? =
  ifOrNull(lhsType == compiled.type) {
    compiled(expression(variable(lhsType)), rhsType)
  }

