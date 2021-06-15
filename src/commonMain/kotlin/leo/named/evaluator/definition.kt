package leo.named.evaluator

import leo.Type
import leo.base.notNullIf
import leo.named.value.ValueLine
import leo.named.value.name
import leo.named.value.value
import leo.type

data class Definition(val type: Type, val binding: Binding) { override fun toString() = scriptLine.toString() }

fun definition(type: Type, binding: Binding) =
	Definition(type, binding)

fun Definition.bindingOrNull(type: Type): Binding? =
	notNullIf(this.type == type) { binding }

val ValueLine.definition: Definition get() =
	definition(name.type, binding(value(this)))

fun Definition.recursive(dictionary: Dictionary): Definition =
	definition(type, binding(recursive(binding, dictionary)))