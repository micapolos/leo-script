package leo.type.compiler

import leo.Dict
import leo.Type
import leo.dict

@kotlin.jvm.JvmInline
value class TypeDictionary(val dict: Dict<Type, Type>)

fun typeDictionary() = TypeDictionary(dict())

fun TypeDictionary.typeOrNull(type: Type): Type? = dict.get(type)
