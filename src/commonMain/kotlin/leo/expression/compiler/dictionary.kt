package leo.expression.compiler

import leo.Dict
import leo.TypeLine
import leo.TypeStructure
import leo.dict
import leo.name
import leo.typeStructure

@kotlin.jvm.JvmInline
value class Dictionary(val dict: Dict<TypeStructure, Binding>)

val Dict<TypeStructure, Binding>.dictionary: Dictionary get() = Dictionary(this)

fun dictionary() = Dictionary(dict())

fun Dictionary.bindingOrNull(typeStructure: TypeStructure): Binding? =
	dict.get(typeStructure)

fun Dictionary.dynamicPlusConstantBinding(typeLine: TypeLine): Dictionary =
	dict
		.put(typeLine.name.typeStructure to typeLine.constantBinding)
		.dictionary