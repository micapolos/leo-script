package leo.named.compiler

import leo.Type
import leo.TypeField
import leo.TypeFunction
import leo.TypeLine
import leo.atom
import leo.base.orNullIf
import leo.givenName
import leo.line
import leo.lineTo
import leo.name
import leo.type

data class Definition(val type: Type, val binding: Binding) {
  override fun toString() = scriptLine.toString()
}

fun definition(type: Type, binding: Binding) =
  Definition(type, binding)

val TypeLine.bindDefinition: Definition
  get() =
    definition(type(name), constantBinding(type(this)))

fun Definition.bindingOrNull(type: Type): Binding? =
  binding.orNullIf(this.type != type)

val TypeFunction.definition: Definition
  get() =
    definition(lhsType, functionBinding(rhsType))

val TypeField.definition: Definition
  get() =
    atom.line.bindDefinition

val Type.givenDefinition: Definition
  get() =
    (givenName lineTo this).bindDefinition
