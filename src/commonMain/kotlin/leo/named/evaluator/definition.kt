package leo.named.evaluator

import leo.Type
import leo.base.notNullIf
import leo.givenName
import leo.named.value.Value
import leo.named.value.ValueLine
import leo.named.value.lineTo
import leo.named.value.name
import leo.named.value.value
import leo.type

data class Definition(val type: Type, val binding: Binding) {
  override fun toString() = scriptLine.toString()
}

fun definition(type: Type, binding: Binding) =
  Definition(type, binding)

fun Definition.bindingOrNull(type: Type): Binding? =
  notNullIf(this.type == type) { binding }

val ValueLine.definition: Definition
  get() =
    definition(name.type, binding(value(this)))

val Value.givenDefinition: Definition
  get() =
    (givenName lineTo this).definition

fun Definition.recursive(base: Dictionary, dictionary: Dictionary): Definition =
  definition(type, binding(recursive(binding, base, dictionary)))

fun Definition.set(dictionary: Dictionary): Definition =
  definition(type, binding.setRecursive(dictionary))
