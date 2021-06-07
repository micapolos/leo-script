package leo.type.compiler

import leo.Script
import leo.Type
import leo.TypeStructure
import leo.get

data class TypeContext(val dictionary: TypeDictionary)

fun context() = TypeContext(typeDictionary())

fun TypeContext.type(script: Script): Type =
	typeCompilation(script).get(this)

fun TypeContext.structureOrNull(structure: TypeStructure): TypeStructure? =
	dictionary.structureOrNull(structure)

fun TypeContext.structure(name: String): TypeStructure =
	null
		?: dictionary.structureOrNull(name)
		?: name.nameResolveTypeStructure