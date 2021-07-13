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

sealed class Binding
data class ConstantBinding(val constant: Constant) : Binding()
data class FunctionBinding(val function: TypeFunction) : Binding()

fun binding(constant: Constant): Binding = ConstantBinding(constant)
fun binding(function: TypeFunction): Binding = FunctionBinding(function)

fun <V> Binding.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  when (this) {
    is FunctionBinding -> function.resolveOrNull(variable, compiled)
    is ConstantBinding -> constant.resolveOrNull(variable, compiled)
  }

fun <V> TypeFunction.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  notNullIf(compiled.type == lhsType) {
    compiled(expression<V>(variable), type(line(atom(this)))).invoke(compiled)
  }
