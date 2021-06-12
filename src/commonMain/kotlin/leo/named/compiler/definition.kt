package leo.named.compiler

import leo.Type
import leo.TypeLine
import leo.base.orNullIf
import leo.name
import leo.type

data class Definition(val structure: Type, val binding: Binding)

fun definition(type: Type, binding: Binding) = Definition(type, binding)
fun TypeLine.nameDefinition(): Definition = definition(type(name), constantBinding(type(this)))

fun Definition.bindingOrNull(type: Type): Binding? =
	binding.orNullIf(this.structure != type)