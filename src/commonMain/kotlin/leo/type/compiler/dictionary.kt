package leo.type.compiler

import leo.Dict
import leo.Type
import leo.dict
import leo.nameOrNull

@kotlin.jvm.JvmInline
value class TypeDictionary(val dict: Dict<String, Type>)

fun typeDictionary() = TypeDictionary(dict())

fun TypeDictionary.typeOrNull(type: Type): Type? =
	type.nameOrNull?.let { name ->
		typeOrNull(name)
	}

fun TypeDictionary.typeOrNull(name: String): Type? =
	dict.get(name)
