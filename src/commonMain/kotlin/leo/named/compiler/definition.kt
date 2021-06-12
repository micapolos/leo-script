package leo.named.compiler

import leo.TypeLine
import leo.TypeStructure
import leo.base.orNullIf
import leo.lineTo
import leo.name
import leo.type
import leo.typeStructure

data class Definition(val structure: TypeStructure, val binding: Binding)

fun definition(type: TypeStructure, binding: Binding) = Definition(type, binding)
fun TypeLine.definition(): Definition = definition(typeStructure(name lineTo type()), constantBinding(this))

fun Definition.bindingOrNull(structure: TypeStructure): Binding? =
	binding.orNullIf(this.structure != structure)