package leo.type.compiler

import leo.Script
import leo.TypeStructure
import leo.get

data class TypeContext(val dictionary: TypeDictionary)

fun context() = TypeContext(typeDictionary())

fun TypeContext.structure(script: Script): TypeStructure =
	structureCompilation(script).get(this)

