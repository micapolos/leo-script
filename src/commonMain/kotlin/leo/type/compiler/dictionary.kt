package leo.type.compiler

import leo.Dict
import leo.Type
import leo.dict

@kotlin.jvm.JvmInline
value class TypeDictionary(val dict: Dict<String, Type>)

fun typeDictionary() = TypeDictionary(dict())