package leo.type.compiler

import leo.Dict
import leo.Type
import leo.TypeStructure
import leo.dict
import leo.lineTo
import leo.nameOrNull
import leo.structure

@kotlin.jvm.JvmInline
value class TypeDictionary(val dict: Dict<String, Type>)

fun typeDictionary() = TypeDictionary(dict())

fun TypeDictionary.structureOrNull(structure: TypeStructure): TypeStructure? =
	structure.nameOrNull?.let { name ->
		structureOrNull(name)
	}

fun TypeDictionary.structureOrNull(name: String): TypeStructure? =
	dict.get(name)?.let { type ->
		structure(name lineTo type)
	}
