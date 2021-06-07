package leo.type.compiler

import leo.Script
import leo.Type
import leo.get

data class TypeContext(val dictionary: TypeDictionary)

fun context() = TypeContext(typeDictionary())

fun TypeContext.type(script: Script): Type =
	typeCompilation(script).get(this)

fun TypeContext.resolveTypeOrNull(type: Type): Type? =
	dictionary.typeOrNull(type)
