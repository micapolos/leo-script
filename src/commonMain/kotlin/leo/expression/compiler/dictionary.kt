package leo.expression.compiler

import leo.Dict
import leo.TypeStructure
import leo.dict

@kotlin.jvm.JvmInline
value class Dictionary(val dict: Dict<TypeStructure, Binding>)

fun dictionary() = Dictionary(dict())

fun Dictionary.bindingOrNull(typeStructure: TypeStructure): Binding? =
	dict.get(typeStructure)
