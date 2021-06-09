package leo.expression.compiler

import leo.Dict
import leo.Type
import leo.TypeLine
import leo.dict
import leo.name
import leo.type
import leo.typeStructure

@kotlin.jvm.JvmInline
value class Dictionary(val dict: Dict<Type, Binding>)

val Dict<Type, Binding>.dictionary: Dictionary get() = Dictionary(this)

fun dictionary() = Dictionary(dict())

fun Dictionary.bindingOrNull(typeStructure: Type): Binding? =
	dict.get(typeStructure)

fun Dictionary.plus(pair: Pair<Type, Binding>): Dictionary =
	dict.put(pair).dictionary

fun Dictionary.dynamicPlusConstantBinding(typeLine: TypeLine): Dictionary =
	dict
		.put(typeLine.name.typeStructure.type to typeLine.constantBinding)
		.dictionary
