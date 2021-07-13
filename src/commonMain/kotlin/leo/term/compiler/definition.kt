package leo.term.compiler

import leo.TypeFunction
import leo.atom
import leo.base.notNullIf
import leo.line
import leo.term.IndexVariable
import leo.term.compiled.Compiled
import leo.term.compiled.compiled
import leo.term.compiled.expression
import leo.term.compiled.invoke
import leo.type

data class Definition(val function: TypeFunction)

fun definition(function: TypeFunction) = Definition(function)

fun <V> Definition.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  notNullIf(compiled.type == function.lhsType) {
    compiled(expression<V>(variable), type(line(atom(function)))).invoke(compiled)
  }