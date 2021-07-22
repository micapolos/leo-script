package leo.typed.compiler

import leo.Type
import leo.TypeFunction
import leo.atom
import leo.base.notNullIf
import leo.line
import leo.lineOrNull
import leo.nameOrNull
import leo.onlyFieldOrNull
import leo.onlyLineOrNull
import leo.structureOrNull
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiled.CompiledChoice
import leo.typed.compiled.compiled
import leo.typed.compiled.compiledChoice
import leo.typed.compiled.expression
import leo.typed.compiled.getOrNull
import leo.typed.compiled.invoke
import leo.typed.compiled.variable

sealed class Binding
data class GivenBinding(val given: TypeGiven) : Binding()
data class ConstantBinding(val constant: Constant) : Binding()
data class FunctionBinding(val function: TypeFunction) : Binding()

data class TypeGiven(val type: Type)

fun binding(constant: Constant): Binding = ConstantBinding(constant)
fun binding(function: TypeFunction): Binding = FunctionBinding(function)
fun binding(given: TypeGiven): Binding = GivenBinding(given)

fun given(type: Type) = TypeGiven(type)

fun <V> Binding.resolveOrNull(compiled: Compiled<V>): Compiled<V>? =
  when (this) {
    is FunctionBinding -> function.resolveOrNull(compiled)
    is ConstantBinding -> constant.resolveOrNull(compiled)
    is GivenBinding -> given.resolveOrNull(compiled)
  }

fun <V> TypeFunction.resolveOrNull(compiled: Compiled<V>): Compiled<V>? =
  notNullIf(compiled.type == lhsType) {
    compiled(expression<V>(variable(lhsType)), type(line(atom(this)))).invoke(compiled)
  }

fun <V> TypeGiven.resolveOrNull(compiled: Compiled<V>): Compiled<V>? =
  compiled.type.nameOrNull?.let { name ->
    type.onlyLineOrNull.let { onlyLineOrNull ->
      if (onlyLineOrNull != null)
        onlyLineOrNull.nameOrNull?.let { lineName ->
          compiled(expression<V>(variable(type(lineName))), type).let { compiled ->
            if (name != lineName) compiled.getOrNull(name)
            else compiled
          }
        }
      else
        type.structureOrNull?.lineOrNull(name)?.let { typeLine ->
          typeLine.nameOrNull?.let { lineName ->
            compiled(expression(variable(type(lineName))), type(typeLine))
          }
        }
    }
  }

val Binding.rhsType: Type get() =
  when (this) {
    is ConstantBinding -> constant.rhsType
    is FunctionBinding -> function.rhsType
    is GivenBinding -> given.type
  }

fun <V> Binding.compiledChoiceOrNull(): CompiledChoice<V>? =
  when (this) {
    is ConstantBinding -> null
    is FunctionBinding -> null
    is GivenBinding -> given.compiledChoice()
  }

fun <V> TypeGiven.compiledChoice(): CompiledChoice<V> =
  type.onlyFieldOrNull!!.let { typeField ->
    compiled(expression<V>(variable(type(typeField.name))), type).compiledChoice
  }
