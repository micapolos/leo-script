package leo.indexed.compiler

import leo.TypeLine
import leo.TypeStructure
import leo.base.orNullIf
import leo.lineTo
import leo.name
import leo.type
import leo.typeStructure

data class Definition<out T>(val structure: TypeStructure, val binding: Binding<T>)

fun <T> definition(type: TypeStructure, binding: Binding<T>) = Definition(type, binding)
fun <T> TypeLine.definition(): Definition<T> = definition(typeStructure(name lineTo type()), constantBinding(this))

fun <T> Definition<T>.bindingOrNull(structure: TypeStructure): Binding<T>? =
	binding.orNullIf(this.structure != structure)