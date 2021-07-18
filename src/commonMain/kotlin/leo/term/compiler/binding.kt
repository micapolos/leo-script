package leo.term.compiler

import leo.Type
import leo.TypeFunction
import leo.atom
import leo.base.notNullIf
import leo.line
import leo.lineCount
import leo.nameOrNull
import leo.term.IndexVariable
import leo.term.compiled.Compiled
import leo.term.compiled.compiled
import leo.term.compiled.expression
import leo.term.compiled.getOrNull
import leo.term.compiled.invoke
import leo.type

sealed class Binding
data class GivenBinding(val given: TypeGiven) : Binding()
data class ConstantBinding(val constant: Constant) : Binding()
data class FunctionBinding(val function: TypeFunction) : Binding()

data class TypeGiven(val type: Type)

fun binding(constant: Constant): Binding = ConstantBinding(constant)
fun binding(function: TypeFunction): Binding = FunctionBinding(function)
fun binding(given: TypeGiven): Binding = GivenBinding(given)

fun given(type: Type) = TypeGiven(type)

fun <V> Binding.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  when (this) {
    is FunctionBinding -> function.resolveOrNull(variable, compiled)
    is ConstantBinding -> constant.resolveOrNull(variable, compiled)
    is GivenBinding -> given.resolveOrNull(variable, compiled)
  }

fun <V> TypeFunction.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  notNullIf(compiled.type == lhsType) {
    compiled(expression<V>(variable), type(line(atom(this)))).invoke(compiled)
  }

fun <V> TypeGiven.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  compiled.type.nameOrNull?.let { name ->
    compiled(expression<V>(variable), type).getOrNull(name)
  }

val Binding.indexCount: Int get() =
  when (this) {
    is ConstantBinding -> 1
    is FunctionBinding -> 1
    is GivenBinding -> given.type.lineCount
  }