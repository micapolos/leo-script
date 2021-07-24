package leo.typed.compiler

import leo.IndexVariable
import leo.Type
import leo.TypeFunction
import leo.TypeLine
import leo.atom
import leo.base.notNullIf
import leo.line
import leo.name
import leo.nameOrNull
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiled.CompiledChoice
import leo.typed.compiled.compiled
import leo.typed.compiled.compiledChoice
import leo.typed.compiled.expression
import leo.typed.compiled.invoke
import leo.variable

sealed class Binding
data class GivenBinding(val given: TypeLineGiven) : Binding()
data class ConstantBinding(val constant: Constant) : Binding()
data class FunctionBinding(val function: TypeFunction) : Binding()

data class TypeGiven(val type: Type)
data class TypeLineGiven(val typeLine: TypeLine)

fun binding(constant: Constant): Binding = ConstantBinding(constant)
fun binding(function: TypeFunction): Binding = FunctionBinding(function)
fun binding(given: TypeLineGiven): Binding = GivenBinding(given)

fun given(type: Type) = TypeGiven(type)
fun given(typeLine: TypeLine) = TypeLineGiven(typeLine)

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

fun <V> TypeLineGiven.resolveOrNull(variable: IndexVariable, compiled: Compiled<V>): Compiled<V>? =
  compiled.type.nameOrNull?.let { name ->
    notNullIf(typeLine.name == name) {
      compiled(expression(variable), type(typeLine))
    }
    // TODO: Deep resolve!!!
  }

val Binding.rhsType: Type get() =
  when (this) {
    is ConstantBinding -> constant.rhsType
    is FunctionBinding -> function.rhsType
    is GivenBinding -> type(given.typeLine)
  }

fun <V> Binding.compiledChoiceOrNull(): CompiledChoice<V>? =
  when (this) {
    is ConstantBinding -> null
    is FunctionBinding -> null
    is GivenBinding -> given.compiledChoice()
  }

fun <V> TypeLineGiven.compiledChoice(): CompiledChoice<V> =
  compiled(expression<V>(variable(0)), type(typeLine)).compiledChoice
